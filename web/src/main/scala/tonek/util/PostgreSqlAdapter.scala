package tonek.util

import java.sql.Timestamp
import java.util.Date

/**
 * @author anton.safonov
 */
class PostgreSqlAdapter extends org.squeryl.adapters.PostgreSqlAdapter{
  override def convertToJdbcValue(r: AnyRef) = {
    var v = r
    if(v.isInstanceOf[Product1[_]])
      v = v.asInstanceOf[Product1[Any]]._1.asInstanceOf[AnyRef]

    if(v.isInstanceOf[java.util.Date] && !v.isInstanceOf[java.sql.Date]  && !v.isInstanceOf[Timestamp]) {
      new Timestamp(v.asInstanceOf[Date].getTime)
    } else {
      super.convertToJdbcValue(r)
    }
  }
}
