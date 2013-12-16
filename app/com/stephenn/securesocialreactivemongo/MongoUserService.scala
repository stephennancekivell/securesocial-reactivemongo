package com.stephenn.securesocialreactivemongo

import _root_.java.util.Date
import securesocial.core._
import play.api.{Logger,Application}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import securesocial.core.IdentityId
import securesocial.core.providers.Token
import play.modules.reactivemongo.MongoController
import play.api.mvc.Controller
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Await
import scala.concurrent.duration._
import reactivemongo.core.commands.GetLastError
import scala.util.parsing.json.JSONObject
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import Formats._

class MongoUserService(application: Application) extends UserServicePlugin(application) with Controller with MongoController {
  def identities: JSONCollection = db.collection[JSONCollection]("identities")
  def tokens: JSONCollection = db.collection[JSONCollection]("tokens")

  val timeout = DurationInt(30).seconds

  def save(user: Identity): Identity = {
    Await.ready(identities.insert(user), timeout)
    user
  }

  def find(id: IdentityId): Option[Identity] = {
    Await.result(identities.find(Json.obj("identityId" -> Json.toJson(id))).one[SocialUser], timeout)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    find(IdentityId(email, providerId))
  }

  def save(token: Token) {
    Await.ready(tokens.insert(token), timeout)
  }

  def findToken(uuid: String): Option[Token] = {
    Await.result(tokens.find(Json.obj("uuid" -> uuid)).one[Token], timeout)
  }

  def deleteToken(uuid: String) {
    Await.ready(tokens.remove(Json.obj("uuid" -> uuid)), timeout)
  }

  def deleteExpiredTokens() {
    Await.ready(tokens.remove(Json.obj("expirationTime" -> Json.obj("$lt" -> Json.obj("$date" -> DateTime.now().getMillis)))), timeout)
  }
}