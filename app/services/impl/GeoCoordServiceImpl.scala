package services.impl

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import scala.concurrent.Future
import javax.inject.Inject
import models.GeoCoord
import models.daos.GeoCoordDao
import services.GeoCoordService
import java.util.UUID

class GeoCoordServiceImpl @Inject() (geoCoordDao: GeoCoordDao) extends GeoCoordService {

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord = {
    geoCoordDao.save(geoCoord, apiKey)
  }

}
