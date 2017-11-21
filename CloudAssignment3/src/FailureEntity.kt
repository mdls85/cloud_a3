import com.microsoft.azure.storage.table.TableServiceEntity
import java.util.*

class FailureEntity(var message:String?): TableServiceEntity() {
    var failureDate:String
    init{
        val dateTime = Calendar.getInstance()
        failureDate = "${dateTime.get(Calendar.DAY_OF_MONTH)}/${dateTime.get(Calendar.MONTH) +1}/${dateTime.get(Calendar.YEAR)} at time ${dateTime.get(Calendar.HOUR_OF_DAY)}:${dateTime.get(Calendar.MINUTE)}:${dateTime.get(Calendar.MILLISECOND)}"
        partitionKey="Failed"
        rowKey = System.nanoTime().toString()
        if (message==null)
            message = "INVALID MESSAGE"
    }

}