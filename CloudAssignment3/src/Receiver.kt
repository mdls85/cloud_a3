import com.microsoft.azure.servicebus.*
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.storage.table.TableOperation
import org.codehaus.jettison.json.JSONObject
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.CompletableFuture

val FAILURE = 2 //There is a 2% chance that a message will fail
val MAXPROCESS = 100 // server will batch process 100 messages from the queue in any given cycle


val ENDPOINT = "Endpoint=sb://second-bus.servicebus.windows.net/;SharedAccessKeyName=all;SharedAccessKey=wKW8JBxSkA0MjP8btryAyGfJNsw1FTiJY7gluofABnM=;EntityPath=queue"



fun main(args:Array<String>){
    //Set up the variables for the sending and receiving messages from the queue
    val queueReceiver = ClientFactory.createMessageReceiverFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT),ReceiveMode.RECEIVEANDDELETE)
    val queueSender = ClientFactory.createMessageSenderFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT))
    val currentTask = CompletableFuture<IMessage>()

    HandleStorage.createTable()

    try {
        CompletableFuture.runAsync {
            while (!currentTask.isCancelled) {
                try {
                    queueReceiver.receiveBatch(MAXPROCESS)
                        .forEach {
                            if (it==null){
                                handleFailure()
                            }
                            else if (!shouldReportFalseMessage()){
                                handleMessage(String(it.body, UTF_8))

                            } else{
                                handleFailure(it,queueSender)//should return message to queue here
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

}


fun handleFailure(message: IMessage?=null, queueSender: IMessageSender?=null) {
    //return message to queue here
    queueSender?.send(message)
    //println("Failure-->")
    //track failure
    val fEntity = FailureEntity(message?.messageId)
    HandleStorage.cloudTable?.execute(TableOperation.insertOrReplace(fEntity))
}

private fun handleMessage(string: String) {
    val obj = JSONObject(string)
    //  println(string)
    val tEntity = TransactionEntity(obj.getString("UserId"),obj.getString("SellerID"),obj.getString("Product Name"),obj.getDouble("Sale Price"),obj.getString("Transaction Date"))
    HandleStorage.cloudTable?.execute(TableOperation.insertOrReplace(tEntity))
}

private fun shouldReportFalseMessage():Boolean = Random().nextInt(100) +  1 <= FAILURE
