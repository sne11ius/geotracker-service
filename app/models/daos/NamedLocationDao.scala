package models.daos

import models.User
import models.NamedLocation

trait NamedLocationDao {

  def addLocation(location: NamedLocation, user: User)

  def loadLocations(user: User): List[NamedLocation]

  def find(user: User, locationId: Long): Option[NamedLocation]
}
