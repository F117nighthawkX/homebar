package dev.nighthawklabs.homebar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.nighthawklabs.homebar.ui.inventory.InventoryScreen
import dev.nighthawklabs.homebar.ui.recipes.detail.RecipeDetailScreen
import dev.nighthawklabs.homebar.ui.recipes.list.RecipeListScreen
import dev.nighthawklabs.homebar.ui.settings.SettingsScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object HomeBarRoute {
    const val RecipeList = "recipe_list"
    const val RecipeListWithIngredient = "$RecipeList?ingredientId={ingredientId}"
    const val RecipeDetail = "recipe_detail/{recipeId}"
    const val Inventory = "inventory"
    const val Settings = "settings"

    fun recipeDetail(recipeId: String): String = "recipe_detail/$recipeId"

    fun recipeList(ingredientId: String? = null): String = ingredientId?.let {
        "$RecipeList?ingredientId=${URLEncoder.encode(it, StandardCharsets.UTF_8)}"
    } ?: RecipeList
}

@Composable
fun HomeBarNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeBarRoute.RecipeList) {
        composable(
            route = HomeBarRoute.RecipeListWithIngredient,
            arguments = listOf(
                navArgument("ingredientId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            RecipeListScreen(
                initialIngredientId = backStackEntry.arguments?.getString("ingredientId"),
                onRecipeSelected = { navController.navigate(HomeBarRoute.recipeDetail(it)) },
                onInventorySelected = { navController.navigate(HomeBarRoute.Inventory) },
                onSettingsSelected = { navController.navigate(HomeBarRoute.Settings) },
            )
        }
        composable(
            route = HomeBarRoute.RecipeDetail,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType }),
        ) { backStackEntry ->
            RecipeDetailScreen(
                recipeId = checkNotNull(backStackEntry.arguments?.getString("recipeId")),
                onBack = { navController.popBackStack() },
            )
        }
        composable(HomeBarRoute.Inventory) {
            InventoryScreen(onBack = { navController.popBackStack() })
        }
        composable(HomeBarRoute.Settings) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
