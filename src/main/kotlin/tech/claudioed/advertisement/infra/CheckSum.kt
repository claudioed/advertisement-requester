package tech.claudioed.advertisement.infra

import org.apache.commons.codec.digest.DigestUtils


/**
 * @author claudioed on 08/03/20.
 * Project starter
 */
class CheckSum(private val data:String) {

  fun checkSum(): String = DigestUtils.md5Hex(data)

}
