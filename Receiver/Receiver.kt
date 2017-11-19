import com.microsoft.azure.servicebus.*
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import com.microsoft.azure.servicebus.IMessage
import org.codehaus.jettison.json.JSONObject
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisShardInfo
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.CompletableFuture

val FAILURE = 2 //There is a 2% chance that a message will fail
val MAXPROCESS = 100 // server will batch process 100 messages from the queue in any given cycle

// todo - need to write a script that changes these value and get it from the template... maybe include them in the args for the jar file..?

val ENDPOINT = "Endpoint=sb://second-bus.servicebus.windows.net/;SharedAccessKeyName=test;SharedAccessKey=UKoJV2DMgw38m+cE1v9rmwqhADsgT/Ke0Vq95Ly1KsE=;EntityPath=queue"
val REDISIP = "52.168.39.207"
val REDISPORT = 6379
val REDISPWD = "P#RZKyg%s05jO&Q4vnOr"
// end todo


fun main(args:Array<String>){
    //Set up the variables for the sending and receiving messages from the queue
    val queueReceiver = ClientFactory.createMessageReceiverFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT),ReceiveMode.RECEIVEANDDELETE)
    val queueSender = ClientFactory.createMessageSenderFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT))
    val currentTask = CompletableFuture<IMessage>()

    //Set up variables for the Redis server
    val redisInfo = JedisShardInfo(REDISIP,REDISPORT)
    redisInfo.password= REDISPWD
    val redisSvr = Jedis(redisInfo)
    redisSvr.connect()

    try {
        CompletableFuture.runAsync {
            while (!currentTask.isCancelled) {
                try {
                    queueReceiver.receiveBatch(MAXPROCESS)
                        .forEach {
                            if (it==null){
                                handleFailure(redisSvr = redisSvr)
                            }
                            else if (!shouldReportFalseMessage()){
                                handleMessage(String(it.body, UTF_8),redisSvr)

                            } else{
                                handleFailure(it,queueSender,redisSvr)//should return message to queue here
                            }
                        }
                } catch (e: Exception) {
                    //If error, end the program
                    currentTask.completeExceptionally(e)
                }
            }
            if (!currentTask.isCancelled) {
                //If error, end the program
                currentTask.complete(null)
            }
        }
    } catch (e: Exception) {
        //If error, end the program
        currentTask.completeExceptionally(e)
    }
    finally{
        //Disconnect from the server once program has ended
        if (redisSvr.isConnected){
            redisSvr.disconnect()
        }
    }

}


fun handleFailure(message: IMessage?=null, queueSender: IMessageSender?=null, redisSvr: Jedis) {
    //return message to queue here
    queueSender?.send(message)

    //track failure inside redis
    val dateTime = Calendar.getInstance()
    redisSvr.lpush("failure","at ${dateTime.get(Calendar.DAY_OF_MONTH)}/${dateTime.get(Calendar.MONTH) +1}/${dateTime.get(Calendar.YEAR)} at time ${dateTime.get(Calendar.HOUR_OF_DAY)}:${dateTime.get(Calendar.MINUTE)}:${dateTime.get(Calendar.MILLISECOND)} for message ${message?.messageId}")
}

private fun handleMessage(string: String, redisSvr: Jedis) {
    val obj = JSONObject(string)
    redisSvr.lpush(obj.getString("TransactionID"),string) // push the entire json string to the redis server
}

private fun shouldReportFalseMessage():Boolean = Random().nextInt(100) +  1 <= FAILURE

