package models.daos

import models.User
import models.NamedLocation
import org.joda.time.Interval
import models.GeoCoord

trait NamedLocationDao {

  def addLocation(location: NamedLocation, user: User)

  def loadLocations(user: User): List[NamedLocation]

  def find(user: User, locationId: Long): Option[NamedLocation]

  def delete(location: NamedLocation)
}
