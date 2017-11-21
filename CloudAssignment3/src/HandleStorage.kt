import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.table.CloudTable

class HandleStorage {

    companion object {
        var cloudTable : CloudTable?=null

        // Define the connection-string with your values.
        private val storageConnectionString =
                "DefaultEndpointsProtocol=https;" +
                        "AccountName=19013258542961;" +
                        "AccountKey=eVI2H5wQ86TV0/+K5Ew3E8Mb9ZzuMRxhC7XrB5gqLqkE7D/G/5h1HP3tj0mSpZlS7Hj45UkH7W2VA0CpNkp8ew=="

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