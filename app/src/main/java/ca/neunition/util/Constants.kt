/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Global constants to be used for the app.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.util

import ca.neunition.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Singleton

@Singleton
object Constants {
    val FIREBASE_AUTH: FirebaseAuth by lazy { Firebase.auth }

    val EDAMAM_API_ID: String by lazy { BuildConfig.EDAMAM_API_ID }
    val EDAMAM_API_KEY: String by lazy { BuildConfig.EDAMAM_API_KEY }

    val BREAKFAST_CHANNEL_ID: String by lazy { "BREAKFAST_NOTIFICATION" }
    val BREAKFAST_NOTIFICATION_NAME: String by lazy { "Breakfast Reminder" }
    val BREAKFAST_NOTIFICATION_ID: Int by lazy { 111 }
    val BREAKFAST_CONTEXT_TEXT: String by lazy { "There's nothing like starting the day with an environmentally friendly breakfast. Let's record the GHG emissions for what you ate!" }
    val BREAKFAST_CHANNEL_DESCRIPTION: String by lazy { "Reminder to record your GHG emissions for breakfast." }

    val LUNCH_CHANNEL_ID: String by lazy { "LUNCH_NOTIFICATION" }
    val LUNCH_NOTIFICATION_NAME: String by lazy { "Lunch Reminder" }
    val LUNCH_NOTIFICATION_ID: Int by lazy { 222 }
    val LUNCH_CONTEXT_TEXT: String by lazy { "You've been working very hard. Take a break and recharge with a meal that helps fight climate change! Remember to record your GHG emissions for lunch." }
    val LUNCH_CHANNEL_DESCRIPTION: String by lazy { "Reminder to record your GHG emissions for lunch." }

    val DINNER_CHANNEL_ID: String by lazy { "DINNER_NOTIFICATION" }
    val DINNER_NOTIFICATION_NAME: String by lazy { "Dinner Reminder" }
    val DINNER_NOTIFICATION_ID: Int by lazy { 333 }
    val DINNER_CONTEXT_TEXT: String by lazy { "Is it? Not sure, but hopefully it's filled with ingredients that can save our planet! Just need to record your GHG emissions one more time before the day ends!" }
    val DINNER_CHANNEL_DESCRIPTION: String by lazy { "Reminder to record your GHG emissions for dinner." }

    // CO2 scores for each ingredient per gram
    val INGREDIENTS: HashMap<String, Double> by lazy {
        hashMapOf(
            "beef" to 0.0596,
            "beefs" to 0.0596,
            "steak" to 0.0596,
            "steaks" to 0.0596,
            "beefsteak" to 0.0596,
            "beefsteaks" to 0.0596,
            "sirloin" to 0.0596,
            "sirloins" to 0.0596,
            "entrecote" to 0.0596,
            "entrecotes" to 0.0596,
            "entrecôte" to 0.0596,
            "entrecôtes" to 0.0596,
            "pastrami" to 0.0596,
            "pastramis" to 0.0596,
            "smoked meat" to 0.0596,
            "smoked meats" to 0.0596,
            "chuck" to 0.0596,
            "veal" to 0.0596,
            "veals" to 0.0596,
            "ribeye" to 0.0596,
            "ribeyes" to 0.0596,
            "brisket" to 0.0596,
            "briskets" to 0.0596,
            "oxtail" to 0.0596,
            "oxtails" to 0.0596,
            "short rib" to 0.0596,
            "short ribs" to 0.0596,
            "filet mignon" to 0.0596,
            "filets mignons" to 0.0596,
            "suet" to 0.0596,
            "suets" to 0.0596,
            "lamb" to 0.0245,
            "lambs" to 0.0245,
            "mutton" to 0.0245,
            "muttons" to 0.0245,
            "cheese" to 0.0212,
            "cheeses" to 0.0212,
            "cheddar" to 0.0212,
            "cheddars" to 0.0212,
            "parmesan" to 0.0212,
            "parmesans" to 0.0212,
            "mozzarella" to 0.0212,
            "mozzarellas" to 0.0212,
            "mascarpone" to 0.0212,
            "mascarpones" to 0.0212,
            "feta" to 0.0212,
            "fetas" to 0.0212,
            "asiago" to 0.0212,
            "asiagos" to 0.0212,
            "provolone" to 0.0212,
            "provolones" to 0.0212,
            "cotija" to 0.0212,
            "curd" to 0.0212,
            "curds" to 0.0212,
            "paneer" to 0.0212,
            "paneers" to 0.0212,
            "chhena" to 0.0212,
            "chhana" to 0.0212,
            "kefalotyri" to 0.0212,
            "reblochon" to 0.0212,
            "reblochons" to 0.0212,
            "grana padano" to 0.0212,
            "parmigiano reggiano" to 0.0212,
            "camembert" to 0.0212,
            "camemberts" to 0.0212,
            "monterey jack" to 0.0212,
            "monterey jacks" to 0.0212,
            "chocolate" to 0.0187,
            "chocolates" to 0.0187,
            "coffee" to 0.0165,
            "coffees" to 0.0165,
            "prawn" to 0.0118,
            "prawns" to 0.0118,
            "shrimp" to 0.0118,
            "shrimps" to 0.0118,
            "lobster" to 0.0118,
            "lobsters" to 0.0118,
            "crab" to 0.0118,
            "crabs" to 0.0118,
            "crayfish" to 0.0118,
            "mollusk" to 0.0118,
            "mollusks" to 0.0118,
            "scallop" to 0.0118,
            "scallops" to 0.0118,
            "clam" to 0.0118,
            "clams" to 0.0118,
            "oyster" to 0.0118,
            "oysters" to 0.0118,
            "cockle" to 0.0118,
            "cockles" to 0.0118,
            "mussel" to 0.0118,
            "mussels" to 0.0118,
            "snail" to 0.0118,
            "snails" to 0.0118,
            "squid" to 0.0118,
            "squids" to 0.0118,
            "krill" to 0.0118,
            "barnacle" to 0.0118,
            "barnacles" to 0.0118,
            "copepod" to 0.0118,
            "copepods" to 0.0118,
            "amphipoda" to 0.0118,
            "amphipodas" to 0.0118,
            "backfin" to 0.0118,
            "backfins" to 0.0118,
            "octopus" to 0.0118,
            "octopuses" to 0.0118,
            "octopi" to 0.0118,
            "octopodes" to 0.0118,
            "cuttlefish" to 0.0118,
            "cuttlefishes" to 0.0118,
            "saewoo jeot" to 0.0118,
            "saeu jeot" to 0.0118,
            "palm oil" to 0.0076,
            "bacon" to 0.0072,
            "bacons" to 0.0072,
            "pork" to 0.0072,
            "porks" to 0.0072,
            "lardon" to 0.0072,
            "lardons" to 0.0072,
            "chorizo" to 0.0072,
            "chorizos" to 0.0072,
            "capicola" to 0.0072,
            "capicolas" to 0.0072,
            "ham" to 0.0072,
            "hams" to 0.0072,
            "prosciutto" to 0.0072,
            "prosciuttos" to 0.0072,
            "prosciutti" to 0.0072,
            "sausage" to 0.0072,
            "sausages" to 0.0072,
            "mortadella" to 0.0072,
            "mortadellas" to 0.0072,
            "andouille" to 0.0072,
            "andouilles" to 0.0072,
            "spam" to 0.0072,
            "bratwurst" to 0.0072,
            "bratwursts" to 0.0072,
            "kielbasa" to 0.0072,
            "kielbasas" to 0.0072,
            "kielbasy" to 0.0072,
            "salami" to 0.0072,
            "salamis" to 0.0072,
            "pancetta" to 0.0072,
            "pancettas" to 0.0072,
            "pepperoni" to 0.0072,
            "pepperonis" to 0.0072,
            "trotter" to 0.0072,
            "trotters" to 0.0072,
            "baby back rib" to 0.0072,
            "baby back ribs" to 0.0072,
            "spareribs" to 0.0072,
            "spare ribs" to 0.0072,
            "chicken" to 0.0061,
            "chickens" to 0.0061,
            "turkey" to 0.0061,
            "turkeys" to 0.0061,
            "duck" to 0.0061,
            "ducks" to 0.0061,
            "squab" to 0.0061,
            "squabs" to 0.0061,
            "giblets" to 0.0061,
            "soybean oil" to 0.006,
            "olive oil" to 0.006,
            "fish" to 0.0051,
            "fishes" to 0.0051,
            "trout" to 0.0051,
            "trouts" to 0.0051,
            "salmon" to 0.0051,
            "salmons" to 0.0051,
            "herring" to 0.0051,
            "herrings" to 0.0051,
            "cod" to 0.0051,
            "cods" to 0.0051,
            "codfish" to 0.0051,
            "codfishes" to 0.0051,
            "roe" to 0.0051,
            "roes" to 0.0051,
            "bacalhau" to 0.0051,
            "bacalhaus" to 0.0051,
            "turbot" to 0.0051,
            "turbots" to 0.0051,
            "redfish" to 0.0051,
            "redfishes" to 0.0051,
            "eel" to 0.0051,
            "eels" to 0.0051,
            "carp" to 0.0051,
            "carps" to 0.0051,
            "catfish" to 0.0051,
            "catfishes" to 0.0051,
            "haddock" to 0.0051,
            "haddocks" to 0.0051,
            "sardine" to 0.0051,
            "sardines" to 0.0051,
            "snapper" to 0.0051,
            "snappers" to 0.0051,
            "albacore" to 0.0051,
            "albacores" to 0.0051,
            "bass" to 0.0051,
            "basses" to 0.0051,
            "grouper" to 0.0051,
            "groupers" to 0.0051,
            "niboshi" to 0.0051,
            "anchovy" to 0.0051,
            "anchovies" to 0.0051,
            "tuna" to 0.0051,
            "tunas" to 0.0051,
            "mackerel" to 0.0051,
            "mackerels" to 0.0051,
            "katsuobushi" to 0.0051,
            "plaice" to 0.0051,
            "plaices" to 0.0051,
            "whitefish" to 0.0051,
            "whitefishes" to 0.0051,
            "pike" to 0.0051,
            "pikes" to 0.0051,
            "kipper" to 0.0051,
            "kippers" to 0.0051,
            "mullet" to 0.0051,
            "mullets" to 0.0051,
            "egg" to 0.0045,
            "eggs" to 0.0045,
            "rice" to 0.004,
            "rices" to 0.004,
            "basmati" to 0.004,
            "rapeseed oil" to 0.0037,
            "sunflower oil" to 0.0035,
            "sunflower seed oil" to 0.0035,
            "tofu" to 0.003,
            "tofus" to 0.003,
            "dairy milk" to 0.0028,
            "cow milk" to 0.0028,
            "2% milk" to 0.0028,
            "3.25% milk" to 0.0028,
            "whole milk" to 0.0028,
            "powdered milk" to 0.0028,
            "powder milk" to 0.0028,
            "milk powder" to 0.0028,
            "dried milk" to 0.0028,
            "dry milk" to 0.0028,
            "fat milk" to 0.0028,
            "buttermilk" to 0.0028,
            "buttermilks" to 0.0028,
            "heavy cream" to 0.0028,
            "heavy creams" to 0.0028,
            "whipping cream" to 0.0028,
            "whipped cream" to 0.0028,
            "whipped creams" to 0.0028,
            "double cream" to 0.0028,
            "double creams" to 0.0028,
            "sour cream" to 0.0028,
            "sour creams" to 0.0028,
            "crème fraîche" to 0.0028,
            "creme fraiche" to 0.0028,
            "crèmes fraiches" to 0.0028,
            "half and half" to 0.0028,
            "butter" to 0.0028,
            "butters" to 0.0028,
            "ghee" to 0.0028,
            "ghees" to 0.0028,
            "yogurt" to 0.0028,
            "yoghurt" to 0.0028,
            "yogurts" to 0.0028,
            "dahi" to 0.0028,
            "sugar" to 0.0026,
            "sugars" to 0.0026,
            "candy" to 0.0026,
            "candies" to 0.0026,
            "jellybean" to 0.0026,
            "jelly bean" to 0.0026,
            "jelly beans" to 0.0026,
            "molasses" to 0.0026,
            "treacle" to 0.0026,
            "treacles" to 0.0026,
            "muscovado" to 0.0026,
            "muscovados" to 0.0026,
            "corn syrup" to 0.0026,
            "corn syrups" to 0.0026,
            "golden syrup" to 0.0026,
            "golden syrups" to 0.0026,
            "maple syrup" to 0.0026,
            "maple syrups" to 0.0026,
            "simple syrup" to 0.0026,
            "simple syrups" to 0.0026,
            "piloncillo" to 0.0026,
            "piloncillos" to 0.0026,
            "sprinkle" to 0.0026,
            "sprinkles" to 0.0026,
            "groundnut" to 0.0024,
            "groundnuts" to 0.0024,
            "peanut" to 0.0024,
            "peanuts" to 0.0024,
            "oatmeal" to 0.0016,
            "oatmeals" to 0.0016,
            "oat" to 0.0016,
            "oats" to 0.0016,
            "bean" to 0.0016,
            "beans" to 0.0016,
            "broad bean" to 0.0016,
            "broad beans" to 0.0016,
            "vicia faba" to 0.0016,
            "fava bean" to 0.0016,
            "fava beans" to 0.0016,
            "cannellini" to 0.0016,
            "cannellinis" to 0.0016,
            "edamame" to 0.0016,
            "edamames" to 0.0016,
            "soybean" to 0.0016,
            "soybeans" to 0.0016,
            "lentil" to 0.0016,
            "lentils" to 0.0016,
            "masoor" to 0.0016,
            "chickpea" to 0.0016,
            "chickpeas" to 0.0016,
            "besan" to 0.0016,
            "besans" to 0.0016,
            "besane" to 0.0016,
            "lupine" to 0.0016,
            "lupines" to 0.0016,
            "wheat" to 0.0014,
            "wheats" to 0.0014,
            "bread" to 0.0014,
            "breads" to 0.0014,
            "bun" to 0.0014,
            "buns" to 0.0014,
            "sourdough" to 0.0014,
            "sourdoughs" to 0.0014,
            "rye" to 0.0014,
            "ryes" to 0.0014,
            "tortilla" to 0.0014,
            "tortillas" to 0.0014,
            "bolillo" to 0.0014,
            "bolillos" to 0.0014,
            "baguette" to 0.0014,
            "baguettes" to 0.0014,
            "loaf" to 0.0014,
            "loaves" to 0.0014,
            "breadcrumbs" to 0.0014,
            "ciabatta" to 0.0014,
            "ciabattas" to 0.0014,
            "ciabatte" to 0.0014,
            "corn tortilla" to 0.0014,
            "corn tortillas" to 0.0014,
            "matzo" to 0.0014,
            "matzos" to 0.0014,
            "matzot" to 0.0014,
            "matzoh" to 0.0014,
            "matzoth" to 0.0014,
            "matza" to 0.0014,
            "matzas" to 0.0014,
            "matzah" to 0.0014,
            "matzahs" to 0.0014,
            "pita" to 0.0014,
            "pitas" to 0.0014,
            "pitta" to 0.0014,
            "pittas" to 0.0014,
            "filo" to 0.0014,
            "filos" to 0.0014,
            "phyllo" to 0.0014,
            "phyllos" to 0.0014,
            "hoagie roll" to 0.0014,
            "hoagie rolls" to 0.0014,
            "roti" to 0.0014,
            "rotis" to 0.0014,
            "flatbread" to 0.0014,
            "flatbreads" to 0.0014,
            "beet sugar" to 0.0014,
            "beet sugars" to 0.0014,
            "tomato" to 0.0014,
            "tomatoes" to 0.0014,
            "cherry tomato" to 0.0014,
            "cherry tomatoes" to 0.0014,
            "wine" to 0.0014,
            "wines" to 0.0014,
            "sherry" to 0.0014,
            "corn" to 0.0011,
            "corns" to 0.0011,
            "maize" to 0.0011,
            "beer" to 0.0011,
            "beers" to 0.0011,
            "ale" to 0.0011,
            "ales" to 0.0011,
            "lager" to 0.0011,
            "lagers" to 0.0011,
            "berry" to 0.0011,
            "berries" to 0.0011,
            "blackberry" to 0.0011,
            "blackberries" to 0.0011,
            "blackcurrant" to 0.0011,
            "blackcurrants" to 0.0011,
            "blueberry" to 0.0011,
            "blueberries" to 0.0011,
            "cranberry" to 0.0011,
            "cranberries" to 0.0011,
            "elderberry" to 0.0011,
            "elderberries" to 0.0011,
            "gooseberry" to 0.0011,
            "gooseberries" to 0.0011,
            "mulberry" to 0.0011,
            "mulberries" to 0.0011,
            "raspberry" to 0.0011,
            "raspberries" to 0.0011,
            "strawberry" to 0.0011,
            "strawberries" to 0.0011,
            "grape" to 0.0011,
            "grapes" to 0.0011,
            "soymilk" to 0.001,
            "soymilks" to 0.001,
            "soy milk" to 0.001,
            "cassava" to 0.0009,
            "cassavas" to 0.0009,
            "pea" to 0.0008,
            "peas" to 0.0008,
            "banana" to 0.0008,
            "bananas" to 0.0008,
            "fruit" to 0.0007,
            "fruits" to 0.0007,
            "acerola" to 0.0007,
            "acerolas" to 0.0007,
            "apricot" to 0.0007,
            "apricots" to 0.0007,
            "avocado" to 0.0007,
            "avocados" to 0.0007,
            "breadfruit" to 0.0007,
            "cantaloup" to 0.0007,
            "cantaloupe" to 0.0007,
            "cantaloups" to 0.0007,
            "cantaloupes" to 0.0007,
            "carambola" to 0.0007,
            "carambolas" to 0.0007,
            "cherimoya" to 0.0007,
            "cherimoyas" to 0.0007,
            "cherry" to 0.0007,
            "cherries" to 0.0007,
            "coconut" to 0.0007,
            "coconuts" to 0.0007,
            "custard apple" to 0.0007,
            "custard apples" to 0.0007,
            "sweetsop" to 0.0007,
            "sweetsops" to 0.0007,
            "sweet sop" to 0.0007,
            "sweet sops" to 0.0007,
            "date" to 0.0007,
            "dates" to 0.0007,
            "durian" to 0.0007,
            "durians" to 0.0007,
            "feijoa" to 0.0007,
            "feijoas" to 0.0007,
            "fig" to 0.0007,
            "figs" to 0.0007,
            "guava" to 0.0007,
            "guavas" to 0.0007,
            "honeydew" to 0.0007,
            "honeydews" to 0.0007,
            "jackfruit" to 0.0007,
            "jackfruits" to 0.0007,
            "jujube" to 0.0007,
            "jujubes" to 0.0007,
            "kiwifruit" to 0.0007,
            "kiwifruits" to 0.0007,
            "longan" to 0.0007,
            "longans" to 0.0007,
            "loquat" to 0.0007,
            "loquats" to 0.0007,
            "lychee" to 0.0007,
            "lychees" to 0.0007,
            "lytchis" to 0.0007,
            "mango" to 0.0007,
            "mangoes" to 0.0007,
            "mangos" to 0.0007,
            "mangosteen" to 0.0007,
            "mangosteens" to 0.0007,
            "nectarine" to 0.0007,
            "nectarines" to 0.0007,
            "olive" to 0.0007,
            "olives" to 0.0007,
            "papaya" to 0.0007,
            "papayas" to 0.0007,
            "passion fruit" to 0.0007,
            "passion fruits" to 0.0007,
            "peach" to 0.0007,
            "peaches" to 0.0007,
            "pear" to 0.0007,
            "pears" to 0.0007,
            "persimmon" to 0.0007,
            "persimmons" to 0.0007,
            "pitaya" to 0.0007,
            "pitayas" to 0.0007,
            "pitahaya" to 0.0007,
            "pitahayas" to 0.0007,
            "dragonfruit" to 0.0007,
            "dragonfruits" to 0.0007,
            "dragon fruit" to 0.0007,
            "dragon fruits" to 0.0007,
            "pineapple" to 0.0007,
            "pineapples" to 0.0007,
            "pitanga" to 0.0007,
            "pitangas" to 0.0007,
            "plantain" to 0.0007,
            "plantains" to 0.0007,
            "plum" to 0.0007,
            "plums" to 0.0007,
            "pomegranate" to 0.0007,
            "pomegranates" to 0.0007,
            "prune" to 0.0007,
            "prunes" to 0.0007,
            "quince" to 0.0007,
            "quinces" to 0.0007,
            "rhubarb" to 0.0007,
            "rhubarbs" to 0.0007,
            "sapodilla" to 0.0007,
            "sapodillas" to 0.0007,
            "mamey sapote" to 0.0007,
            "mamey sapotes" to 0.0007,
            "soursop" to 0.0007,
            "soursops" to 0.0007,
            "tamarind" to 0.0007,
            "tamarinds" to 0.0007,
            "watermelon" to 0.0007,
            "watermelons" to 0.0007,
            "vegetable" to 0.0005,
            "vegetables" to 0.0005,
            "arrowroot" to 0.0005,
            "arrowroots" to 0.0005,
            "artichoke" to 0.0005,
            "artichokes" to 0.0005,
            "arugula" to 0.0005,
            "arugulas" to 0.0005,
            "asparagus" to 0.0005,
            "bamboo shoot" to 0.0005,
            "bamboo shoots" to 0.0005,
            "bell pepper" to 0.0005,
            "bell peppers" to 0.0005,
            "red pepper" to 0.0005,
            "red peppers" to 0.0005,
            "yellow pepper" to 0.0005,
            "yellow peppers" to 0.0005,
            "green pepper" to 0.0005,
            "green peppers" to 0.0005,
            "banana pepper" to 0.0005,
            "banana peppers" to 0.0005,
            "lambs lettuce" to 0.0005,
            "valerianella locusta" to 0.0005,
            "corn salad" to 0.0005,
            "cornsalad" to 0.0005,
            "cornsalads" to 0.0005,
            "mâche" to 0.0005,
            "mache" to 0.0005,
            "beet" to 0.0005,
            "beets" to 0.0005,
            "bitter melon" to 0.0005,
            "bittermelon" to 0.0005,
            "bitter gourd" to 0.0005,
            "bitter gourds" to 0.0005,
            "bok choy" to 0.0005,
            "broccoli" to 0.0005,
            "broccolis" to 0.0005,
            "rapini" to 0.0005,
            "brussel sprouts" to 0.0005,
            "brussels sprouts" to 0.0005,
            "cabbage" to 0.0005,
            "cabbages" to 0.0005,
            "carrot" to 0.0005,
            "carrots" to 0.0005,
            "cauliflower" to 0.0005,
            "cauliflowers" to 0.0005,
            "celeriac" to 0.0005,
            "celeriacs" to 0.0005,
            "celery" to 0.0005,
            "celeries" to 0.0005,
            "chayote" to 0.0005,
            "chayotes" to 0.0005,
            "chicory" to 0.0005,
            "chicories" to 0.0005,
            "collard" to 0.0005,
            "collards" to 0.0005,
            "crookneck" to 0.0005,
            "crooknecks" to 0.0005,
            "cucumber" to 0.0005,
            "cucumbers" to 0.0005,
            "daikon" to 0.0005,
            "daikons" to 0.0005,
            "eggplant" to 0.0005,
            "eggplants" to 0.0005,
            "endive" to 0.0005,
            "endives" to 0.0005,
            "fennel" to 0.0005,
            "fennels" to 0.0005,
            "fiddlehead" to 0.0005,
            "fiddleheads" to 0.0005,
            "ginger" to 0.0005,
            "gingers" to 0.0005,
            "horseradish" to 0.0005,
            "horseradishes" to 0.0005,
            "jicama" to 0.0005,
            "jicamas" to 0.0005,
            "kale" to 0.0005,
            "kales" to 0.0005,
            "kohlrabi" to 0.0005,
            "kohlrabies" to 0.0005,
            "lettuce" to 0.0005,
            "lettuces" to 0.0005,
            "mushroom" to 0.0005,
            "mushrooms" to 0.0005,
            "brassica juncea" to 0.0005,
            "mustard greens" to 0.0005,
            "okra" to 0.0005,
            "okras" to 0.0005,
            "parsnip" to 0.0005,
            "parsnips" to 0.0005,
            "pumpkin" to 0.0005,
            "pumpkins" to 0.0005,
            "radicchio" to 0.0005,
            "radicchios" to 0.0005,
            "radish" to 0.0005,
            "radishes" to 0.0005,
            "rutabaga" to 0.0005,
            "rutabagas" to 0.0005,
            "salsify" to 0.0005,
            "salsifies" to 0.0005,
            "shallot" to 0.0005,
            "shallots" to 0.0005,
            "sorrel" to 0.0005,
            "sorrels" to 0.0005,
            "squash" to 0.0005,
            "squashes" to 0.0005,
            "spinach" to 0.0005,
            "spinaches" to 0.0005,
            "chard" to 0.0005,
            "chards" to 0.0005,
            "tomatillo" to 0.0005,
            "tomatillos" to 0.0005,
            "tomatilloes" to 0.0005,
            "turnip" to 0.0005,
            "turnips" to 0.0005,
            "watercress" to 0.0005,
            "watercresses" to 0.0005,
            "yam" to 0.0005,
            "yams" to 0.0005,
            "zucchini" to 0.0005,
            "zucchinis" to 0.0005,
            "brassica" to 0.0004,
            "brassicas" to 0.0004,
            "potato" to 0.0003,
            "potatoes" to 0.0003,
            "onion" to 0.0003,
            "onions" to 0.0003,
            "leek" to 0.0003,
            "leeks" to 0.0003,
            "citrus fruit" to 0.0003,
            "citrus fruits" to 0.0003,
            "citrus" to 0.0003,
            "amanatsu" to 0.0003,
            "citron" to 0.0003,
            "citrons" to 0.0003,
            "orange" to 0.0003,
            "oranges" to 0.0003,
            "buddhas hand" to 0.0003,
            "buddhas hands" to 0.0003,
            "calamondin" to 0.0003,
            "calamondins" to 0.0003,
            "cam sành" to 0.0003,
            "citrange" to 0.0003,
            "citranges" to 0.0003,
            "citrumelo" to 0.0003,
            "citrumelos" to 0.0003,
            "clementine" to 0.0003,
            "clementines" to 0.0003,
            "lime" to 0.0003,
            "limes" to 0.0003,
            "etrog" to 0.0003,
            "ethrog" to 0.0003,
            "esrog" to 0.0003,
            "etrogim" to 0.0003,
            "ethrogim" to 0.0003,
            "esrogim" to 0.0003,
            "etrogs" to 0.0003,
            "ethrogs" to 0.0003,
            "esrogs" to 0.0003,
            "grapefruit" to 0.0003,
            "grapefruits" to 0.0003,
            "haruka" to 0.0003,
            "hassaku" to 0.0003,
            "hyuganatsu" to 0.0003,
            "jabara" to 0.0003,
            "kabosu" to 0.0003,
            "kanpei" to 0.0003,
            "kawachi bankan" to 0.0003,
            "kinkoji unshiu" to 0.0003,
            "kinnow" to 0.0003,
            "kiyomi" to 0.0003,
            "kobayashi mikan" to 0.0003,
            "kumquat" to 0.0003,
            "kumquats" to 0.0003,
            "lemon" to 0.0003,
            "lemons" to 0.0003,
            "lumia" to 0.0003,
            "lumias" to 0.0003,
            "mandarin" to 0.0003,
            "mandarins" to 0.0003,
            "mandarine" to 0.0003,
            "mandarines" to 0.0003,
            "mangshanyegan" to 0.0003,
            "orangelo" to 0.0003,
            "orangelos" to 0.0003,
            "oroblanco" to 0.0003,
            "oroblancos" to 0.0003,
            "oro blanco" to 0.0003,
            "pomelit" to 0.0003,
            "papeda" to 0.0003,
            "papedas" to 0.0003,
            "pomelo" to 0.0003,
            "pomelos" to 0.0003,
            "pompia" to 0.0003,
            "pumpia" to 0.0003,
            "ponkan" to 0.0003,
            "ponkans" to 0.0003,
            "rangpur" to 0.0003,
            "rangpurs" to 0.0003,
            "satsuma" to 0.0003,
            "satsumas" to 0.0003,
            "shangjuan" to 0.0003,
            "shonan gold" to 0.0003,
            "sudachi" to 0.0003,
            "tangerine" to 0.0003,
            "tangerines" to 0.0003,
            "tangelo" to 0.0003,
            "tangelos" to 0.0003,
            "yūkō" to 0.0003,
            "yukou" to 0.0003,
            "yuzu" to 0.0003,
            "yuzus" to 0.0003,
            "apple" to 0.0003,
            "apples" to 0.0003,
            "nut" to 0.0002,
            "nuts" to 0.0002,
            "hazelnut" to 0.0002,
            "hazelnuts" to 0.0002,
            "chestnut" to 0.0002,
            "chestnuts" to 0.0002,
            "pecan" to 0.0002,
            "pecans" to 0.0002,
            "walnut" to 0.0002,
            "walnuts" to 0.0002
        )
    }

    val TWO_WORD_INGREDIENTS: HashSet<String> by lazy {
        hashSetOf(
            "smoked",
            "short",
            "filet",
            "filets",
            "grana",
            "parmigiano",
            "monterey",
            "saewoo",
            "saeu",
            "palm",
            "spare",
            "soybean",
            "olive",
            "rapeseed",
            "sunflower",
            "dairy",
            "cow",
            "2%",
            "3.25%",
            "whole",
            "powdered",
            "powder",
            "milk",
            "dried",
            "dry",
            "fat",
            "heavy",
            "whipping",
            "whipped",
            "double",
            "sour",
            "crème",
            "creme",
            "crèmes",
            "jelly",
            "corn",
            "golden",
            "maple",
            "simple",
            "broad",
            "vicia",
            "fava",
            "hoagie",
            "beet",
            "cherry",
            "soy",
            "custard",
            "sweet",
            "passion",
            "dragon",
            "mamey",
            "bamboo",
            "bell",
            "red",
            "yellow",
            "green",
            "banana",
            "lambs",
            "valerianella",
            "bitter",
            "bok",
            "brussel",
            "brussels",
            "brassica",
            "mustard",
            "citrus",
            "buddhas",
            "cam",
            "kawachi",
            "kinkoji",
            "kobayashi",
            "oro",
            "shonan",
        )
    }

    val THREE_WORD_INGREDIENTS: HashSet<String> by lazy {
        hashSetOf(
            "baby",
            "sunflower",
            "half"
        )
    }

    val LABELS: Array<String> by lazy {
        arrayOf(
            "Alcohol-Cocktail",
            "Alcohol-Free",
            "Balanced",
            "Celery-Free",
            "Crustacean-Free",
            "Dairy-Free",
            "Egg-Free",
            "Fish-Free",
            "Gluten-Free",
            "High-Fiber",
            "High-Protein",
            "Immuno-Supportive",
            "Keto-Friendly",
            "Kidney-Friendly",
            "Kosher",
            "Low-Carb",
            "Low-Fat",
            "Low-Potassium",
            "Low-Sodium",
            "Low-Sugar",
            "Lupine-Free",
            "Mediterranean",
            "Mollusk-Free",
            "Mustard-Free",
            "Paleo",
            "Peanut-Free",
            "Pescatarian",
            "Pork-Free",
            "Red-Meat-Free",
            "Sesame-Free",
            "Shellfish-Free",
            "Soy-Free",
            "Sugar-Conscious",
            "Sulfite-Free",
            "Tree-Nut-Free",
            "Vegan",
            "Vegetarian",
            "Wheat-Free"
        )
    }

    val DIET_PARAMETERS: HashSet<String> by lazy {
        hashSetOf(
            "balanced",
            "high-fiber",
            "high-protein",
            "low-carb",
            "low-fat",
            "low-sodium"
        )
    }

    val WEIGHT_OPTIONS: List<String> by lazy {
        listOf(
            "mg",
            "g",
            "kg",
            "tsp",
            "tbsp",
            "cups",
            "lbs",
            "oz",
            "fl oz",
            "mL",
            "L",
            "gal",
            "eggs"
        )
    }

    val WEIGHTS: HashSet<String> by lazy {
        hashSetOf(
            "mg",
            "milligram",
            "milligrams",
            "g",
            "gram",
            "grams",
            "kg",
            "kgs",
            "kilogram",
            "kilograms",
            "kilo",
            "kilos",
            "tsp",
            "tsps",
            "teaspoon",
            "teaspoons",
            "tbsp",
            "tbsps",
            "tablespoon",
            "tablespoons",
            "cup",
            "cups",
            "lb",
            "lbs",
            "pound",
            "pounds",
            "oz",
            "ounce",
            "ounces",
            //"fl oz",
            //"fluid ounce",
            //"fluid ounces",
            "ml",
            "milliliter",
            "milliliters",
            "millilitre",
            "millilitres",
            "l",
            "liter",
            "liters",
            "litre",
            "litres",
            "gal",
            "gallon",
            "gallons",
            "egg",
            "eggs"
        )
    }
}
