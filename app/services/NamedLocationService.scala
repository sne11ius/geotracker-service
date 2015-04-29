package services

import java.util.UUID
import models.User
import models.NamedLocation

trait NamedLocationService {

  def loadLocations(user: User): List[NamedLocation]

}
