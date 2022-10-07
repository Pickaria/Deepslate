package fr.pickaria

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.* //NEEDED! import KMongo extensions
import java.time.LocalDate
import java.time.Month

@Serializable //you need to annotate each class you want to persist
data class Jedi(val name: String, val age: Int, val firstAppearance: StarWarsFilm)

@Serializable
data class StarWarsFilm(
	val name: String,
	//annotate with @Contextual the types that have already serializers - look at kotlinx.serialization documentation
	@Contextual val date: LocalDate
)

fun testDatabase() {
	val client = KMongo.createClient("mongodb://root:root@localhost:27017/") //get com.mongodb.MongoClient new instance
	val database = client.getDatabase("test") //normal java driver usage
	val col = database.getCollection<Jedi>() //KMongo extension method
	//here the name of the collection by convention is "jedi"
	//you can use getCollection<Jedi>("otherjedi") if the collection name is different

	col.insertOne(Jedi("Luke Skywalker", 19, StarWarsFilm("A New Hope", LocalDate.of(1977, Month.MAY, 25))))

	val yoda : Jedi? = col.findOne(Jedi::name eq "Yoda")

	println(yoda)
}
