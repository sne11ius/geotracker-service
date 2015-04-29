package models.daos.slick

import models.User
import play.api.Play.current
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Logger
import javax.inject.Inject
import models.daos.NamedLocationDao
import models.NamedLocation

class NamedLocationDaoSlick @Inject() () extends NamedLocationDao {

  override def loadLocations(user: User): List[NamedLocation] = {
    DB withSession { implicit session =>
      List()
    }
  }

}
