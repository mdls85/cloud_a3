import com.microsoft.azure.servicebus.*
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.storage.table.TableBatchOperation
import com.microsoft.azure.storage.table.TableOperation
import org.codehaus.jettison.json.JSONObject
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.CompletableFuture

val FAILURE = 2 //There is a 2% chance that a message will fail
val MAXPROCESS = 130 // server will batch process 1000 messages from the queue in any given cycle


val ENDPOINT = "Endpoint=sb://highmarks4e90967c5a1.servicebus.windows.net/;SharedAccessKeyName=test;SharedAccessKey=bvMxMmsRURgFqhlwvbkK5qc8rRzxUrhC91yNOMwCiE0=;EntityPath=queue"



fun main(args:Array<String>){
    //Set up the variables for the sending and receiving messages from the queue
    val queueReceiver = ClientFactory.createMessageReceiverFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT),ReceiveMode.RECEIVEANDDELETE)
    val queueSender = ClientFactory.createMessageSenderFromConnectionStringBuilder(ConnectionStringBuilder(ENDPOINT))
    val currentTask = CompletableFuture<IMessage>()
    var total =0
    HandleStorage.createTable()
    val startTime = System.currentTimeMillis()
    var endTime = startTime
    try {
        CompletableFuture.runAsync {
            while (!currentTask.isCancelled) {
                try {
                    val batch = queueReceiver.receiveBatch(MAXPROCESS)
                    val tableBatchOperation = TableBatchOperation()
                    for (it in batch){
                        if (it==null || shouldReportFalseMessage()){
                            handleFailure(it,queueSender)
                        }
                        else{
                            tableBatchOperation.insertOrMerge(getTableEntity(String(it.body,UTF_8)))
                        }
                    }
                    HandleStorage.cloudTable!!.execute(tableBatchOperation)
                    total+=tableBatchOperation.size
                    endTime = (System.currentTimeMillis() - startTime)/1000

                    println("Batchinserting ${tableBatchOperation.size}/${batch.size}\t Total=$total\t Time: ${endTime/60}:${endTime%60}")
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

private fun getTableEntity(string: String) :TransactionEntity{
    val obj = JSONObject(string)
    //  println(string)
    return TransactionEntity(obj.getString("UserId"),obj.getString("SellerID"),obj.getString("Product Name"),obj.getDouble("Sale Price"),obj.getString("Transaction Date"))
}

private fun shouldReportFalseMessage():Boolean = Random().nextInt(100) +  1 <= FAILURE
