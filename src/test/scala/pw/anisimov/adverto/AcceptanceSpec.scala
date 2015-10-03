package pw.anisimov.adverto

import akka.http.scaladsl.model.Uri
import org.scalatest.{BeforeAndAfter, FeatureSpec, GivenWhenThen}

class AcceptanceSpec extends FeatureSpec with GivenWhenThen with BeforeAndAfter {
  var serviceUri: Uri = _

  before {

  }

  after {

  }

  feature("Car Service should provide REST api") {
    info("As a user of the Car Advert Service ")
    info("I want be able to interact with REST API")

    scenario("have functionality to return list of all car adverts") {
      fail()
    }

    scenario("have functionality to return data for single car advert by id") {
      fail()
    }

    scenario("have functionality to add car advert") {
      fail()
    }

    scenario("have functionality to modify car advert by id") {
      fail()
    }

    scenario("have functionality to delete car advert by id") {
      fail()
    }

    scenario("have validation by fields and fields used only for used cars") {
      fail()
    }

    scenario("accept and return data in JSON format") {
      fail()
    }

    scenario("Service should be able to handle CORS requests from any domain") {
      fail()
    }

    scenario("sorting by any field specified by query parameter, default sorting - by **id**") {
      fail()
    }
  }
}
