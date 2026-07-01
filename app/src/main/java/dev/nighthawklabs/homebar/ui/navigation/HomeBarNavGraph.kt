package dev.nighthawklabs.homebar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.nighthawklabs.homebar.ui.inventory.InventoryScreen
import dev.nighthawklabs.homebar.ui.inventory.detail.IngredientDetailScreen
import dev.nighthawklabs.homebar.ui.recipes.detail.RecipeDetailScreen
import dev.nighthawklabs.homebar.ui.recipes.editor.RecipeEditorScreen
import dev.nighthawklabs.homebar.ui.recipes.list.RecipeListScreen
import dev.nighthawklabs.homebar.ui.settings.SettingsScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object HomeBarRoute {
    const val RecipeList = "recipe_list"
    const val RecipeListWithIngredient = "$RecipeList?ingredientId={ingredientId}"
    const val RecipeDetail = "recipe_detail/{recipeId}"
    const val NewRecipe = "recipe_editor/new"
    const val EditRecipe = "recipe_editor/edit/{recipeId}"
    const val Inventory = "inventory"
    const val IngredientDetail = "ingredient_detail/{ingredientId}"
    const val Settings = "settings"

    fun recipeDetail(recipeId: String): String = "recipe_detail/$recipeId"

    fun newRecipe(): String = NewRecipe

    fun editRecipe(recipeId: String): String = "recipe_editor/edit/${encodeRouteValue(recipeId)}"

    fun ingredientDetail(ingredientId: String): String =
        "ingredient_detail/${encodeRouteValue(ingredientId)}"

    fun recipeList(ingredientId: String? = null): String = ingredientId?.let {
        "$RecipeList?ingredientId=${encodeRouteValue(it)}"
    } ?: RecipeList

    private fun encodeRouteValue(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8.name())
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
                onAddRecipe = { navController.navigate(HomeBarRoute.newRecipe()) },
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
                onEditRecipe = { recipeId ->
                    navController.navigate(HomeBarRoute.editRecipe(recipeId))
                },
            )
        }
        composable(HomeBarRoute.NewRecipe) {
            RecipeEditorScreen(
                recipeId = null,
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = HomeBarRoute.EditRecipe,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType }),
        ) { backStackEntry ->
            RecipeEditorScreen(
                recipeId = checkNotNull(backStackEntry.arguments?.getString("recipeId")),
                onBack = { navController.popBackStack() },
            )
        }
        composable(HomeBarRoute.Inventory) {
            InventoryScreen(
                onBack = { navController.popBackStack() },
                onIngredientSelected = { ingredientId ->
                    navController.navigate(HomeBarRoute.ingredientDetail(ingredientId))
                },
            )
        }
        composable(
            route = HomeBarRoute.IngredientDetail,
            arguments = listOf(navArgument("ingredientId") { type = NavType.StringType }),
        ) { backStackEntry ->
            IngredientDetailScreen(
                ingredientId = checkNotNull(backStackEntry.arguments?.getString("ingredientId")),
                onBack = { navController.popBackStack() },
                onViewRecipesUsingIngredient = { ingredientId ->
                    navController.navigate(HomeBarRoute.recipeList(ingredientId))
                },
            )
        }
        composable(HomeBarRoute.Settings) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
