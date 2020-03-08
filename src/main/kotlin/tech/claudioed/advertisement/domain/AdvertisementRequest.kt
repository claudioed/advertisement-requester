package tech.claudioed.advertisement.domain

/**
 * @author claudioed on 07/03/20.
 * Project starter
 */
data class AdvertisementRequest(val id: String, val category: String, val keywords: List<String>,
                                val timeout: Int,val budget:Budget,val requester:Requester)

data class Budget(val initialValue:Double,val maxValue:Double)

data class Requester(val id:String)
