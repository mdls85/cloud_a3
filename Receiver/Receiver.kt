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
val ENDPOINT = "Endpoint=sb://second-bus.servicebus.windows.net/;SharedAccessKeyName=test;SharedAccessKey=UKoJV2DMgw38m+cE1v9rmwqhADsgT/Ke0Vq95Ly1KsE=;EntityPath=queue"
val MAXPROCESS = 100
val REDISIP = "52.168.39.207"
val REDISPORT = 6379
val REDISPWD = "P#RZKyg%s05jO&Q4vnOr"
fun main(args:Array<String>){
    val queueReceiver = ClientFactory.createMessageReceiverFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT),ReceiveMode.RECEIVEANDDELETE)
    val queueSender = ClientFactory.createMessageSenderFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT))
    val currentTask = CompletableFuture<IMessage>()
    val redisInfo = JedisShardInfo(REDISIP,REDISPORT)
    redisInfo.password= REDISPWD

    val redisSvr = Jedis(redisInfo)
    redisSvr.connect()

    try {
        CompletableFuture.runAsync {
            while (!currentTask.isCancelled) {
                try {
                    queueReceiver.receiveBatch(MAXPROCESS)
                        .filterNotNull()
                        .forEach {
                            if (!shouldReportFalseMessage()){
                                handleMessage(String(it.body, UTF_8),redisSvr)

                            } else{
                                handleFailure(it,queueSender,redisSvr)//should return message to queue here
                            }
                        }
                } catch (e: Exception) {
                    currentTask.completeExceptionally(e)
                }
            }
            if (!currentTask.isCancelled) {
                currentTask.complete(null)
            }
        }
    } catch (e: Exception) {
        currentTask.completeExceptionally(e)
    }
    finally{
        if (redisSvr.isConnected){
            redisSvr.disconnect()
        }
    }

}


fun handleFailure(message: IMessage, queueSender: IMessageSender, redisSvr: Jedis) {
    //return message to queue here
    queueSender.send(message)

    //track failure inside redis
    redisSvr.lpush("failure","at ${System.currentTimeMillis()} for message ${message.messageId}")
}

private fun handleMessage(string: String, redisSvr: Jedis) {
    val obj = JSONObject(string)
    redisSvr.lpush(obj.getString("TransactionID"),string)
}

private fun shouldReportFalseMessage():Boolean = Random().nextInt(100) +  1 <= FAILURE
