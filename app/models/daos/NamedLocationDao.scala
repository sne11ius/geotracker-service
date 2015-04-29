package models.daos

import models.User
import models.NamedLocation

trait NamedLocationDao {

  def loadLocations(user: User): List[NamedLocation]

}
