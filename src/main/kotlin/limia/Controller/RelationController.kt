package limia.Controller

import limia.Dto.Movie
import limia.Dto.Relation
import limia.Dto.User
import limia.Exception.EntityNotFoundException
import limia.Service.MovieService
import limia.Service.RelationService
import limia.Service.UserService
import spark.Request
import java.util.*

/**
 * Created by macbook on 8/4/17.
 */
class RelationController {

    private val relationService: RelationService = RelationService()
    private val userService: UserService = UserService()
    private val movieService: MovieService = MovieService()


    fun existsRelation(request: Request): Boolean? {
        val relationName = request.queryParams("relation")
        val firstSplat = request.splat()[0]
        val secondSplat = request.splat()[1]

        if (firstSplat.equals("users") && secondSplat.equals("movies")) {
            val userID = request.params(":id0");
            val movieID = request.params(":id1");
            if (relationName.equals("download")) {
                return relationService.exists(relationName, userID, movieID, "downloaded_a", "is_downloaded_by");
            }
            if (relationName.equals("favorite")) {
                return relationService.exists(relationName, userID, movieID, "favorited_a", "is_favorited_by");
            }
        }
        return false
    }


    fun readAll(): ArrayList<Relation> {
        return relationService.readAll();
    }

    fun readRelationByID(identifier: String): Relation? {
        return relationService.readByID(identifier)
    }


    @Throws(EntityNotFoundException::class)
    fun createRelation(request: Request): Relation? {
        val relationName = request.queryParams("relation")
        val firstSplat = request.splat()[0]
        val secondSplat = request.splat()[1]

        if (firstSplat.equals("users") && secondSplat.equals("movies")) {
            var user: User? = null
            var throwNotFoundException = false
            var entityNotFoundException = EntityNotFoundException()
            try {
                user = userService.read(request.params(":id0"))
            } catch(e: EntityNotFoundException) {
                throwNotFoundException = true
                entityNotFoundException.types.add(User::class)
            }
            
            var movie: Movie? = null
            try {
                movie = movieService.read(request.params(":id1"))
            } catch(e: EntityNotFoundException) {
                throwNotFoundException = true
                entityNotFoundException.types.add(Movie::class)
            }

            if (throwNotFoundException)
                throw entityNotFoundException

            if  (user != null && movie != null) {
                if (relationName.equals("download")) {
                    return relationService.create(request.params(":id0"), request.params(":id1"),
                            "downloaded_a", "is_downloaded_by", relationName)
                }
                if (relationName.equals("favorite")) {
                    return relationService.create(request.params(":id0"), request.params(":id1"),
                            "favorited_a", "is_favorited_by", relationName)
                }
            }
        }
        return null
    }

    fun findAllRelationsByName(request: Request): ArrayList<Relation> {
        val relationName = request.params(":name")
        if (relationName.equals("download") || relationName.equals("favorite")) {
            return relationService.readAllOfSpecificType(relationName)
        }
        return ArrayList()
    }

    fun deleteRelation(request: Request) {
        TODO("Not yet implemented")
    }

    fun updateRelation(request: Request) {
        TODO("Not yet implemented")
    }

}
