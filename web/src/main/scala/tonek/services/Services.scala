package tonek.services

/**
 * @author anton.safonov
 */
object Services {
  @volatile
  var search: SearchService = new SearchService()

}
