package services

import java.util.UUID
import models.User
import models.NamedLocation
import models.NamedLocation
import models.User
import org.joda.time.Interval
import models.GeoCoord

trait NamedLocationService {

  def addLocation(location: NamedLocation, user: User)

  def loadLocations(user: User): List[NamedLocation]

  def find(user: User, locationId: Long): Option[NamedLocation]

  def delete(location: NamedLocation)

}
