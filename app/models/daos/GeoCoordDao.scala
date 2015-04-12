package models.daos

import java.util.UUID
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import scala.concurrent.Future
import models.GeoCoord
import models.GeoCoord

trait GeoCoordDao {

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord

  def load(user: User): List[GeoCoord]

  def loadLatest(apiKey: UUID): GeoCoord
  
}
