import com.microsoft.azure.storage.table.TableServiceEntity

class TransactionEntity(var UserID :String, var SellerID:String, var ProductName:String, var SalePrice:Double, var TransactionDate:String) : TableServiceEntity() {
    init {
        partitionKey = "Successful"
        rowKey=System.nanoTime().toString()
    }
}
