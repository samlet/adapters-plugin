import java.sql.Timestamp

import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.base.util.UtilProperties
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.util.EntityUtil
import org.apache.ofbiz.service.ServiceUtil

def getStatusItemsForType() {
    Map result = success()
    // statusTypeId: "ORDER_RETURN_STTS"
    List statusItems = from("StatusItem").where(statusTypeId: parameters.statusTypeId).queryList()
    result.statusItems = statusItems

    return result
}

