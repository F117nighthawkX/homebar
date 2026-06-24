package dev.nighthawklabs.homebar

import android.app.Application
import androidx.room.Room
import dev.nighthawklabs.homebar.data.local.AppDatabase
import dev.nighthawklabs.homebar.data.repository.IngredientRepository
import dev.nighthawklabs.homebar.data.repository.RecipeRepository
import dev.nighthawklabs.homebar.data.repository.RoomIngredientRepository
import dev.nighthawklabs.homebar.data.repository.RoomRecipeRepository
import dev.nighthawklabs.homebar.data.seed.SampleIngredientData
import dev.nighthawklabs.homebar.data.seed.SampleRecipeData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HomeBarApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "home-bar.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    val ingredientRepository: IngredientRepository by lazy {
        RoomIngredientRepository(database.ingredientDao())
    }

    private val roomRecipeRepository: RoomRecipeRepository by lazy {
        RoomRecipeRepository(database.recipeDao())
    }

    val recipeRepository: RecipeRepository by lazy { roomRecipeRepository }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            database.ingredientDao().insertAll(SampleIngredientData.ingredients)
            roomRecipeRepository.insertRecipesIfAbsent(SampleRecipeData.recipes)
        }
    }
}
