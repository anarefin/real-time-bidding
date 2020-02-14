package com.arefin.service

import com.arefin.entity.Campaign
import com.arefin.jsonsupport.{BidRequest, BidResponse, Impression}

import scala.collection.mutable.ListBuffer

object BiddingAgentService {

  //  - campaign.country -> request.Device.geo.country
  //  - campaign.userId -> request.User.id
  //  - campaign.Targeting.cityList -> request.Geo.city
  //  - campaign.targetedSiteIds -> request.Site.id (contains or not)
  //  - campaign.banners.Banner.width and height match with request.Impression.width and height
  //  - campaign.bid >= request.Impression.bidFloor

  def validateBidRequestWithCampaigns(bidRequest: BidRequest, campaignList: List[Campaign]): Option[BidResponse] = {
    //val bidResponse = BidResponse("100", "123", 45.23, None, None)

    // compare running campaigns with request
    val selectedCampaignsForBid: ListBuffer[Campaign] = ListBuffer.empty[Campaign]

    for (campaign <- campaignList) {

      val isCountryMatch = bidRequest.device match {
        case Some(device) => device.geo match {
          case Some(geo) => geo.country match {
            case Some(country) if country.equalsIgnoreCase(campaign.country) => true
            case None => false
            case _ => false
          }
        }
      }

      val isUserIdMatch = bidRequest.user match {
        case Some(user) if user.id == campaign.userId.toString => true
      }

      val isCityMatch = bidRequest.device match {
        case Some(device) => device.geo match {
          case Some(geo) => geo.city match {
            case Some(city) if campaign.targeting.cities.contains(city) => true
            case None => false
            case _ => false
          }
        }
      }
      val isSiteIdMatch = validateSiteIdWithCampaign(bidRequest.site.id, campaign)
      val isBannerMatch = if (bidRequest.imp.isEmpty) false else validateImpressionWithCampaign(bidRequest.imp.get, campaign)


      if(isCountryMatch && isUserIdMatch && isCityMatch && isSiteIdMatch && isBannerMatch){
        selectedCampaignsForBid += campaign
      }
    }

    if(selectedCampaignsForBid.nonEmpty){
      // find the highest bid value in selectedCampaignsForBid
      val bidWinner = selectedCampaignsForBid(0)
      val bidResponse = BidResponse("1",bidRequest.id, bidWinner.bid,None, None)
      return Some(bidResponse)
    }
    return None

  }

  def validateImpressionWithCampaign(impressionList:List[Impression], campaign: Campaign):Boolean = {
    // TODO: convert impressionList such a way so that we can compare/intersect with bannerList
    val result = campaign.banners.intersect(impressionList)  // type will be List[campaign]
    if(result.nonEmpty){
      // TODO: match bid amount also
      return true
    }
    return false
  }

  def validateSiteIdWithCampaign(siteId: Int, campaign: Campaign) : Boolean = {
    // TODO: change List to ListBuffer, as it will store huge data
    campaign.targeting.targetedSiteIds.contains(siteId)
  }
}
