package services.impl

import javax.inject.Inject
import services.NamedLocationService
import models.User
import models.NamedLocation
import models.daos.NamedLocationDao

class NamedLocationServiceImpl @Inject() (namedLocationDao: NamedLocationDao) extends NamedLocationService {

  override def addLocation(location: NamedLocation, user: User) = {
    namedLocationDao.addLocation(location, user)
  }

  override def loadLocations(user: User): List[NamedLocation] = {
    namedLocationDao.loadLocations(user)
  }

  def find(user: User, locationId: Long): Option[NamedLocation] = {
    namedLocationDao.find(user, locationId)
  }

}
