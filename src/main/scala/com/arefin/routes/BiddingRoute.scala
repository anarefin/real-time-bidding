package com.arefin.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post}
import akka.http.scaladsl.server.Route
import com.arefin.entity.CampaignData
import com.arefin.jsonsupport.{BidRequest, JsonSupport}
import com.arefin.service.BiddingAgentService
import com.typesafe.scalalogging.LazyLogging

class BiddingRoute extends JsonSupport with LazyLogging {

  def route(): Route = {
    path("bidding") {
      post {
        entity(as[BidRequest]) { bidRequest =>
          logger.info(s"Bidding request = $bidRequest")
          //complete(StatusCodes.Created, s"Received donut = $bidRequest")
          val bidResponse = BiddingAgentService.validateBidRequestWithCampaigns(bidRequest, CampaignData.getRunningCampaignList())
          if(bidResponse.nonEmpty){
            complete(StatusCodes.OK, bidResponse.get)
          } else {
            complete(StatusCodes.NoContent, "No content!!!")
          }
        }
      }
    }
  }
}
