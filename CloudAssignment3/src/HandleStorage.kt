import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.table.CloudTable

class HandleStorage {

    companion object {
        var cloudTable : CloudTable?=null

        // Define the connection-string with your values.
        private val storageConnectionString ="DefaultEndpointsProtocol=https;AccountName=highmarks4e90967c5a1;AccountKey=melnVizo2qxId1VVy8fEg8xKEhPt2I5ed3pVWapGm4s8EdWR2Cz8px2beTBmEmmEX+OJOeJFSlQS94zlKl/fSw==;EndpointSuffix=core.windows.net"

        private val TABLE_NAME = "request"

        fun createTable() {
            try {
                // Retrieve storage account from connection-string.
                val storageAccount = CloudStorageAccount.parse(storageConnectionString)

                // Create the table client.
                val tableClient = storageAccount.createCloudTableClient()

                // Create the table if it doesn't exist.
                cloudTable = tableClient.getTableReference(TABLE_NAME)
                cloudTable!!.createIfNotExists()

            } catch (e: Exception) {
                // Output the stack trace.
                e.printStackTrace()
            }

        }

    }

}