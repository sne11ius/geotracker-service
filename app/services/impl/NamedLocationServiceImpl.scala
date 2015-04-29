package services.impl

import javax.inject.Inject
import services.NamedLocationService
import models.User
import models.NamedLocation
import models.daos.NamedLocationDao

class NamedLocationServiceImpl @Inject() (namedLocationDao: NamedLocationDao) extends NamedLocationService {

  override def loadLocations(user: User): List[NamedLocation] = {
    namedLocationDao.loadLocations(user)
  }

}
