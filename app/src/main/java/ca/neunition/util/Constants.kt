/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Global constants to be used for the app.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.util

import androidx.browser.customtabs.CustomTabsIntent
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Singleton

@Singleton
object Constants {
    val FIREBASE_AUTH: FirebaseAuth by lazy { Firebase.auth }
    val FIREBASE_DATABASE: FirebaseDatabase by lazy { Firebase.database }

    val BREAKFAST_CHANNEL_ID: String by lazy { "BREAKFAST_NOTIFICATION" }
    val BREAKFAST_NOTIFICATION_ID: Int by lazy { 111 }

    val LUNCH_CHANNEL_ID: String by lazy { "LUNCH_NOTIFICATION" }
    val LUNCH_NOTIFICATION_ID: Int by lazy { 222 }

    val DINNER_CHANNEL_ID: String by lazy { "DINNER_NOTIFICATION" }
    val DINNER_NOTIFICATION_ID: Int by lazy { 333 }

    val CUSTOM_TABS_BUILDER: CustomTabsIntent by lazy { CustomTabsIntent.Builder().build() }

    val AD_REQUEST: AdRequest by lazy { AdRequest.Builder().build() }
    val BANNER_AD_UNIT_ID: String by lazy { "ca-app-pub-2714747137112577/4271938147" }
    val REWARDED_AD_UNIT_ID: String by lazy { "ca-app-pub-2714747137112577/2732831041" }
    val RECIPES_INTERSTITIAL_AD_UNIT_ID: String by lazy { "ca-app-pub-2714747137112577/6439995977" }
    val INGREDIENTS_INTERSTITIAL_AD_UNIT_ID: String by lazy { "ca-app-pub-2714747137112577/4229637746" }

    val REQUEST_OPTIONS: RequestOptions by lazy {
        RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
    }

    // GHG emissions per gram of food product
    val INGREDIENTS: HashMap<String, Double> by lazy {
        hashMapOf(
            "beef" to 0.09948,
            "beefs" to 0.09948,
            "steak" to 0.09948,
            "steaks" to 0.09948,
            "beefsteak" to 0.09948,
            "beefsteaks" to 0.09948,
            "sirloin" to 0.09948,
            "sirloins" to 0.09948,
            "entrecote" to 0.09948,
            "entrecotes" to 0.09948,
            "entrecôte" to 0.09948,
            "entrecôtes" to 0.09948,
            "pastrami" to 0.09948,
            "pastramis" to 0.09948,
            "smoked meat" to 0.09948,
            "smoked meats" to 0.09948,
            "chuck" to 0.09948,
            "veal" to 0.09948,
            "veals" to 0.09948,
            "ribeye" to 0.09948,
            "ribeyes" to 0.09948,
            "brisket" to 0.09948,
            "briskets" to 0.09948,
            "oxtail" to 0.09948,
            "oxtails" to 0.09948,
            "short rib" to 0.09948,
            "short ribs" to 0.09948,
            "filet mignon" to 0.09948,
            "filets mignons" to 0.09948,
            "suet" to 0.09948,
            "suets" to 0.09948,
            "dark chocolate" to 0.04665,
            "dark chocolates" to 0.04665,
            "cocoa" to 0.04665,
            "cocoas" to 0.04665,
            "lamb" to 0.03972,
            "lambs" to 0.03972,
            "mutton" to 0.03972,
            "muttons" to 0.03972,
            "coffee" to 0.02853,
            "coffees" to 0.02853,
            "prawn" to 0.02687,
            "prawns" to 0.02687,
            "shrimp" to 0.02687,
            "shrimps" to 0.02687,
            "lobster" to 0.02687,
            "lobsters" to 0.02687,
            "crab" to 0.02687,
            "crabs" to 0.02687,
            "crayfish" to 0.02687,
            "mollusk" to 0.02687,
            "mollusks" to 0.02687,
            "scallop" to 0.02687,
            "scallops" to 0.02687,
            "clam" to 0.02687,
            "clams" to 0.02687,
            "oyster" to 0.02687,
            "oysters" to 0.02687,
            "cockle" to 0.02687,
            "cockles" to 0.02687,
            "mussel" to 0.02687,
            "mussels" to 0.02687,
            "snail" to 0.02687,
            "snails" to 0.02687,
            "squid" to 0.02687,
            "squids" to 0.02687,
            "krill" to 0.02687,
            "barnacle" to 0.02687,
            "barnacles" to 0.02687,
            "copepod" to 0.02687,
            "copepods" to 0.02687,
            "amphipoda" to 0.02687,
            "amphipodas" to 0.02687,
            "backfin" to 0.02687,
            "backfins" to 0.02687,
            "octopus" to 0.02687,
            "octopuses" to 0.02687,
            "octopi" to 0.02687,
            "octopodes" to 0.02687,
            "cuttlefish" to 0.02687,
            "cuttlefishes" to 0.02687,
            "saewoo jeot" to 0.02687,
            "saeu jeot" to 0.02687,
            "cheese" to 0.02388,
            "cheeses" to 0.02388,
            "cheddar" to 0.02388,
            "cheddars" to 0.02388,
            "parmesan" to 0.02388,
            "parmesans" to 0.02388,
            "mozzarella" to 0.02388,
            "mozzarellas" to 0.02388,
            "mascarpone" to 0.02388,
            "mascarpones" to 0.02388,
            "feta" to 0.02388,
            "fetas" to 0.02388,
            "asiago" to 0.02388,
            "asiagos" to 0.02388,
            "provolone" to 0.02388,
            "provolones" to 0.02388,
            "cotija" to 0.02388,
            "curd" to 0.02388,
            "curds" to 0.02388,
            "paneer" to 0.02388,
            "paneers" to 0.02388,
            "chhena" to 0.02388,
            "chhana" to 0.02388,
            "kefalotyri" to 0.02388,
            "reblochon" to 0.02388,
            "reblochons" to 0.02388,
            "grana padano" to 0.02388,
            "parmigiano reggiano" to 0.02388,
            "camembert" to 0.02388,
            "camemberts" to 0.02388,
            "monterey jack" to 0.02388,
            "monterey jacks" to 0.02388,
            "fish" to 0.01363,
            "fishes" to 0.01363,
            "trout" to 0.01363,
            "trouts" to 0.01363,
            "salmon" to 0.01363,
            "salmons" to 0.01363,
            "herring" to 0.01363,
            "herrings" to 0.01363,
            "cod" to 0.01363,
            "cods" to 0.01363,
            "codfish" to 0.01363,
            "codfishes" to 0.01363,
            "roe" to 0.01363,
            "roes" to 0.01363,
            "bacalhau" to 0.01363,
            "bacalhaus" to 0.01363,
            "turbot" to 0.01363,
            "turbots" to 0.01363,
            "redfish" to 0.01363,
            "redfishes" to 0.01363,
            "eel" to 0.01363,
            "eels" to 0.01363,
            "carp" to 0.01363,
            "carps" to 0.01363,
            "catfish" to 0.01363,
            "catfishes" to 0.01363,
            "haddock" to 0.01363,
            "haddocks" to 0.01363,
            "sardine" to 0.01363,
            "sardines" to 0.01363,
            "snapper" to 0.01363,
            "snappers" to 0.01363,
            "albacore" to 0.01363,
            "albacores" to 0.01363,
            "bass" to 0.01363,
            "basses" to 0.01363,
            "grouper" to 0.01363,
            "groupers" to 0.01363,
            "niboshi" to 0.01363,
            "anchovy" to 0.01363,
            "anchovies" to 0.01363,
            "tuna" to 0.01363,
            "tunas" to 0.01363,
            "mackerel" to 0.01363,
            "mackerels" to 0.01363,
            "katsuobushi" to 0.01363,
            "plaice" to 0.01363,
            "plaices" to 0.01363,
            "whitefish" to 0.01363,
            "whitefishes" to 0.01363,
            "pike" to 0.01363,
            "pikes" to 0.01363,
            "kipper" to 0.01363,
            "kippers" to 0.01363,
            "mullet" to 0.01363,
            "mullets" to 0.01363,
            "bacon" to 0.01231,
            "bacons" to 0.01231,
            "pork" to 0.01231,
            "porks" to 0.01231,
            "lardon" to 0.01231,
            "lardons" to 0.01231,
            "chorizo" to 0.01231,
            "chorizos" to 0.01231,
            "capicola" to 0.01231,
            "capicolas" to 0.01231,
            "ham" to 0.01231,
            "hams" to 0.01231,
            "prosciutto" to 0.01231,
            "prosciuttos" to 0.01231,
            "prosciutti" to 0.01231,
            "sausage" to 0.01231,
            "sausages" to 0.01231,
            "mortadella" to 0.01231,
            "mortadellas" to 0.01231,
            "andouille" to 0.01231,
            "andouilles" to 0.01231,
            "spam" to 0.01231,
            "bratwurst" to 0.01231,
            "bratwursts" to 0.01231,
            "kielbasa" to 0.01231,
            "kielbasas" to 0.01231,
            "kielbasy" to 0.01231,
            "salami" to 0.01231,
            "salamis" to 0.01231,
            "pancetta" to 0.01231,
            "pancettas" to 0.01231,
            "pepperoni" to 0.01231,
            "pepperonis" to 0.01231,
            "trotter" to 0.01231,
            "trotters" to 0.01231,
            "baby back rib" to 0.01231,
            "baby back ribs" to 0.01231,
            "spareribs" to 0.01231,
            "spare ribs" to 0.01231,
            "chicken" to 0.00987,
            "chickens" to 0.00987,
            "turkey" to 0.00987,
            "turkeys" to 0.00987,
            "duck" to 0.00987,
            "ducks" to 0.00987,
            "squab" to 0.00987,
            "squabs" to 0.00987,
            "giblets" to 0.00987,
            "egg" to 0.00467,
            "eggs" to 0.00467,
            "rice" to 0.00445,
            "rices" to 0.00445,
            "basmati" to 0.00445,
            "groundnut" to 0.00323,
            "groundnuts" to 0.00323,
            "peanut" to 0.00323,
            "peanuts" to 0.00323,
            "sugar" to 0.0032,
            "sugars" to 0.0032,
            "candy" to 0.0032,
            "candies" to 0.0032,
            "jellybean" to 0.0032,
            "jelly bean" to 0.0032,
            "jelly beans" to 0.0032,
            "molasses" to 0.0032,
            "treacle" to 0.0032,
            "treacles" to 0.0032,
            "muscovado" to 0.0032,
            "muscovados" to 0.0032,
            "corn syrup" to 0.0032,
            "corn syrups" to 0.0032,
            "golden syrup" to 0.0032,
            "golden syrups" to 0.0032,
            "maple syrup" to 0.0032,
            "maple syrups" to 0.0032,
            "simple syrup" to 0.0032,
            "simple syrups" to 0.0032,
            "piloncillo" to 0.0032,
            "piloncillos" to 0.0032,
            "sprinkle" to 0.0032,
            "sprinkles" to 0.0032,
            "tofu" to 0.00316,
            "tofus" to 0.00316,
            "dairy milk" to 0.00315,
            "cow milk" to 0.00315,
            "2% milk" to 0.00315,
            "3.25% milk" to 0.00315,
            "whole milk" to 0.00315,
            "powdered milk" to 0.00315,
            "powder milk" to 0.00315,
            "milk powder" to 0.00315,
            "dried milk" to 0.00315,
            "dry milk" to 0.00315,
            "fat milk" to 0.00315,
            "buttermilk" to 0.00315,
            "buttermilks" to 0.00315,
            "heavy cream" to 0.00315,
            "heavy creams" to 0.00315,
            "whipping cream" to 0.00315,
            "whipped cream" to 0.00315,
            "whipped creams" to 0.00315,
            "double cream" to 0.00315,
            "double creams" to 0.00315,
            "sour cream" to 0.00315,
            "sour creams" to 0.00315,
            "crème fraîche" to 0.00315,
            "creme fraiche" to 0.00315,
            "crèmes fraiches" to 0.00315,
            "half and half" to 0.00315,
            "butter" to 0.00315,
            "butters" to 0.00315,
            "ghee" to 0.00315,
            "ghees" to 0.00315,
            "yogurt" to 0.00315,
            "yoghurt" to 0.00315,
            "yogurts" to 0.00315,
            "dahi" to 0.00315,
            "chocolate" to 0.00315,
            "chocolates" to 0.00315,
            "oatmeal" to 0.00248,
            "oatmeals" to 0.00248,
            "oat" to 0.00248,
            "oats" to 0.00248,
            "tomato" to 0.00209,
            "tomatoes" to 0.00209,
            "cherry tomato" to 0.00209,
            "cherry tomatoes" to 0.00209,
            "beet sugar" to 0.00181,
            "beet sugars" to 0.00181,
            "bean" to 0.00179,
            "beans" to 0.00179,
            "vicia faba" to 0.00179,
            "cannellini" to 0.00179,
            "cannellinis" to 0.00179,
            "edamame" to 0.00179,
            "edamames" to 0.00179,
            "soybean" to 0.00179,
            "soybeans" to 0.00179,
            "lentil" to 0.00179,
            "lentils" to 0.00179,
            "masoor" to 0.00179,
            "chickpea" to 0.00179,
            "chickpeas" to 0.00179,
            "besan" to 0.00179,
            "besans" to 0.00179,
            "besane" to 0.00179,
            "lupine" to 0.00179,
            "lupines" to 0.00179,
            "wine" to 0.00179,
            "wines" to 0.00179,
            "sherry" to 0.00179,
            "corn" to 0.0017,
            "corns" to 0.0017,
            "maize" to 0.0017,
            "maizes" to 0.0017,
            "wheat" to 0.00157,
            "wheats" to 0.00157,
            "bread" to 0.00157,
            "breads" to 0.00157,
            "bun" to 0.00157,
            "buns" to 0.00157,
            "sourdough" to 0.00157,
            "sourdoughs" to 0.00157,
            "rye" to 0.00157,
            "ryes" to 0.00157,
            "tortilla" to 0.00157,
            "tortillas" to 0.00157,
            "bolillo" to 0.00157,
            "bolillos" to 0.00157,
            "baguette" to 0.00157,
            "baguettes" to 0.00157,
            "loaf" to 0.00157,
            "loaves" to 0.00157,
            "breadcrumbs" to 0.00157,
            "ciabatta" to 0.00157,
            "ciabattas" to 0.00157,
            "ciabatte" to 0.00157,
            "corn tortilla" to 0.00157,
            "corn tortillas" to 0.00157,
            "matzo" to 0.00157,
            "matzos" to 0.00157,
            "matzot" to 0.00157,
            "matzoh" to 0.00157,
            "matzoth" to 0.00157,
            "matza" to 0.00157,
            "matzas" to 0.00157,
            "matzah" to 0.00157,
            "matzahs" to 0.00157,
            "pita" to 0.00157,
            "pitas" to 0.00157,
            "pitta" to 0.00157,
            "pittas" to 0.00157,
            "filo" to 0.00157,
            "filos" to 0.00157,
            "phyllo" to 0.00157,
            "phyllos" to 0.00157,
            "hoagie roll" to 0.00157,
            "hoagie rolls" to 0.00157,
            "roti" to 0.00157,
            "rotis" to 0.00157,
            "flatbread" to 0.00157,
            "flatbreads" to 0.00157,
            "berry" to 0.00153,
            "berries" to 0.00153,
            "blackberry" to 0.00153,
            "blackberries" to 0.00153,
            "blackcurrant" to 0.00153,
            "blackcurrants" to 0.00153,
            "blueberry" to 0.00153,
            "blueberries" to 0.00153,
            "cranberry" to 0.00153,
            "cranberries" to 0.00153,
            "elderberry" to 0.00153,
            "elderberries" to 0.00153,
            "gooseberry" to 0.00153,
            "gooseberries" to 0.00153,
            "mulberry" to 0.00153,
            "mulberries" to 0.00153,
            "raspberry" to 0.00153,
            "raspberries" to 0.00153,
            "strawberry" to 0.00153,
            "strawberries" to 0.00153,
            "grape" to 0.00153,
            "grapes" to 0.00153,
            "cassava" to 0.00132,
            "cassavas" to 0.00132,
            "manihot esculenta" to 0.00132,
            "manioc" to 0.00132,
            "maniocs" to 0.00132,
            "mandioca" to 0.00132,
            "mandiocas" to 0.00132,
            "yuca" to 0.00132,
            "yucas" to 0.00132,
            "barley" to 0.00118,
            "barleys" to 0.00118,
            "fruit" to 0.00105,
            "fruits" to 0.00105,
            "acerola" to 0.00105,
            "acerolas" to 0.00105,
            "apricot" to 0.00105,
            "apricots" to 0.00105,
            "avocado" to 0.00105,
            "avocados" to 0.00105,
            "breadfruit" to 0.00105,
            "cantaloup" to 0.00105,
            "cantaloupe" to 0.00105,
            "cantaloups" to 0.00105,
            "cantaloupes" to 0.00105,
            "carambola" to 0.00105,
            "carambolas" to 0.00105,
            "cherimoya" to 0.00105,
            "cherimoyas" to 0.00105,
            "cherry" to 0.00105,
            "cherries" to 0.00105,
            "coconut" to 0.00105,
            "coconuts" to 0.00105,
            "custard apple" to 0.00105,
            "custard apples" to 0.00105,
            "sweetsop" to 0.00105,
            "sweetsops" to 0.00105,
            "sweet sop" to 0.00105,
            "sweet sops" to 0.00105,
            "date" to 0.00105,
            "dates" to 0.00105,
            "durian" to 0.00105,
            "durians" to 0.00105,
            "feijoa" to 0.00105,
            "feijoas" to 0.00105,
            "fig" to 0.00105,
            "figs" to 0.00105,
            "guava" to 0.00105,
            "guavas" to 0.00105,
            "honeydew" to 0.00105,
            "honeydews" to 0.00105,
            "jackfruit" to 0.00105,
            "jackfruits" to 0.00105,
            "jujube" to 0.00105,
            "jujubes" to 0.00105,
            "kiwifruit" to 0.00105,
            "kiwifruits" to 0.00105,
            "longan" to 0.00105,
            "longans" to 0.00105,
            "loquat" to 0.00105,
            "loquats" to 0.00105,
            "lychee" to 0.00105,
            "lychees" to 0.00105,
            "lytchis" to 0.00105,
            "mango" to 0.00105,
            "mangoes" to 0.00105,
            "mangos" to 0.00105,
            "mangosteen" to 0.00105,
            "mangosteens" to 0.00105,
            "nectarine" to 0.00105,
            "nectarines" to 0.00105,
            "olive" to 0.00105,
            "olives" to 0.00105,
            "papaya" to 0.00105,
            "papayas" to 0.00105,
            "passion fruit" to 0.00105,
            "passion fruits" to 0.00105,
            "peach" to 0.00105,
            "peaches" to 0.00105,
            "pear" to 0.00105,
            "pears" to 0.00105,
            "persimmon" to 0.00105,
            "persimmons" to 0.00105,
            "pitaya" to 0.00105,
            "pitayas" to 0.00105,
            "pitahaya" to 0.00105,
            "pitahayas" to 0.00105,
            "dragonfruit" to 0.00105,
            "dragonfruits" to 0.00105,
            "dragon fruit" to 0.00105,
            "dragon fruits" to 0.00105,
            "pineapple" to 0.00105,
            "pineapples" to 0.00105,
            "pitanga" to 0.00105,
            "pitangas" to 0.00105,
            "plantain" to 0.00105,
            "plantains" to 0.00105,
            "plum" to 0.00105,
            "plums" to 0.00105,
            "pomegranate" to 0.00105,
            "pomegranates" to 0.00105,
            "prune" to 0.00105,
            "prunes" to 0.00105,
            "quince" to 0.00105,
            "quinces" to 0.00105,
            "rhubarb" to 0.00105,
            "rhubarbs" to 0.00105,
            "sapodilla" to 0.00105,
            "sapodillas" to 0.00105,
            "mamey sapote" to 0.00105,
            "mamey sapotes" to 0.00105,
            "soursop" to 0.00105,
            "soursops" to 0.00105,
            "tamarind" to 0.00105,
            "tamarinds" to 0.00105,
            "watermelon" to 0.00105,
            "watermelons" to 0.00105,
            "pea" to 0.00098,
            "peas" to 0.00098,
            "soymilk" to 0.00098,
            "soymilks" to 0.00098,
            "soy milk" to 0.00098,
            "banana" to 0.00086,
            "bananas" to 0.00086,
            "vegetable" to 0.00053,
            "vegetables" to 0.00053,
            "arrowroot" to 0.00053,
            "arrowroots" to 0.00053,
            "artichoke" to 0.00053,
            "artichokes" to 0.00053,
            "asparagus" to 0.00053,
            "bamboo shoot" to 0.00053,
            "bamboo shoots" to 0.00053,
            "bell pepper" to 0.00053,
            "bell peppers" to 0.00053,
            "red pepper" to 0.00053,
            "red peppers" to 0.00053,
            "yellow pepper" to 0.00053,
            "yellow peppers" to 0.00053,
            "green pepper" to 0.00053,
            "green peppers" to 0.00053,
            "banana pepper" to 0.00053,
            "banana peppers" to 0.00053,
            "lambs lettuce" to 0.00053,
            "valerianella locusta" to 0.00053,
            "corn salad" to 0.00053,
            "cornsalad" to 0.00053,
            "cornsalads" to 0.00053,
            "mâche" to 0.00053,
            "mache" to 0.00053,
            "bitter melon" to 0.00053,
            "bittermelon" to 0.00053,
            "bitter gourd" to 0.00053,
            "bitter gourds" to 0.00053,
            "celery" to 0.00053,
            "celeries" to 0.00053,
            "chayote" to 0.00053,
            "chayotes" to 0.00053,
            "chicory" to 0.00053,
            "chicories" to 0.00053,
            "collard" to 0.00053,
            "collards" to 0.00053,
            "crookneck" to 0.00053,
            "crooknecks" to 0.00053,
            "cucumber" to 0.00053,
            "cucumbers" to 0.00053,
            "eggplant" to 0.00053,
            "eggplants" to 0.00053,
            "endive" to 0.00053,
            "endives" to 0.00053,
            "fiddlehead" to 0.00053,
            "fiddleheads" to 0.00053,
            "lettuce" to 0.00053,
            "lettuces" to 0.00053,
            "mushroom" to 0.00053,
            "mushrooms" to 0.00053,
            "okra" to 0.00053,
            "okras" to 0.00053,
            "pumpkin" to 0.00053,
            "pumpkins" to 0.00053,
            "radicchio" to 0.00053,
            "radicchios" to 0.00053,
            "salsify" to 0.00053,
            "salsifies" to 0.00053,
            "sorrel" to 0.00053,
            "sorrels" to 0.00053,
            "squash" to 0.00053,
            "squashes" to 0.00053,
            "spinach" to 0.00053,
            "spinaches" to 0.00053,
            "chard" to 0.00053,
            "chards" to 0.00053,
            "tomatillo" to 0.00053,
            "tomatillos" to 0.00053,
            "tomatilloes" to 0.00053,
            "zucchini" to 0.00053,
            "zucchinis" to 0.00053,
            "brassica" to 0.00051,
            "brassicas" to 0.00051,
            "cabbage" to 0.00051,
            "cabbages" to 0.00051,
            "kale" to 0.00051,
            "kales" to 0.00051,
            "broccoli" to 0.00051,
            "broccolis" to 0.00051,
            "cauliflower" to 0.00051,
            "cauliflowers" to 0.00051,
            "brussels sprout" to 0.00051,
            "brussels sprouts" to 0.00051,
            "mustard greens" to 0.00051,
            "collard greens" to 0.00051,
            "bok choy" to 0.00051,
            "bok choys" to 0.00051,
            "watercress" to 0.00051,
            "watercresses" to 0.00051,
            "romanesco" to 0.00051,
            "romanescos" to 0.00051,
            "rapini" to 0.00051,
            "wasabi" to 0.00051,
            "wasabis" to 0.00051,
            "tatsoi" to 0.00051,
            "spoon mustard" to 0.00051,
            "mizuna" to 0.00051,
            "mizunas" to 0.00051,
            "daikon" to 0.00051,
            "daikons" to 0.00051,
            "arugula" to 0.00051,
            "arugulas" to 0.00051,
            "rapeseed" to 0.00051,
            "canola" to 0.00051,
            "onion" to 0.0005,
            "onions" to 0.0005,
            "shallot" to 0.0005,
            "shallots" to 0.0005,
            "leek" to 0.0005,
            "leeks" to 0.0005,
            "potato" to 0.00046,
            "potatoes" to 0.00046,
            "yam" to 0.00046,
            "yams" to 0.00046,
            "apple" to 0.00043,
            "apples" to 0.00043,
            "nut" to 0.00043,
            "nuts" to 0.00043,
            "hazelnut" to 0.00043,
            "hazelnuts" to 0.00043,
            "filbert" to 0.00043,
            "filberts" to 0.00043,
            "cobnut" to 0.00043,
            "cobnuts" to 0.00043,
            "chestnut" to 0.00043,
            "chestnuts" to 0.00043,
            "pecan" to 0.00043,
            "pecans" to 0.00043,
            "walnut" to 0.00043,
            "walnuts" to 0.00043,
            "almond" to 0.00043,
            "almonds" to 0.00043,
            "cashew" to 0.00043,
            "cashews" to 0.00043,
            "pistachio" to 0.00043,
            "pistachios" to 0.00043,
            "macadamia" to 0.00043,
            "macadamias" to 0.00043,
            "acorn" to 0.00043,
            "acorns" to 0.00043,
            "root vegetable" to 0.00043,
            "root vegetables" to 0.00043,
            "turnip" to 0.00043,
            "turnips" to 0.00043,
            "horseradish" to 0.00043,
            "horseradishes" to 0.00043,
            "radish" to 0.00043,
            "radishes" to 0.00043,
            "celeriac" to 0.00043,
            "celeriacs" to 0.00043,
            "parsnip" to 0.00043,
            "parsnips" to 0.00043,
            "carrot" to 0.00043,
            "carrots" to 0.00043,
            "fennel" to 0.00043,
            "fennels" to 0.00043,
            "crosne" to 0.00043,
            "crosnes" to 0.00043,
            "kohlrabi" to 0.00043,
            "kohlrabies" to 0.00043,
            "turmeric" to 0.00043,
            "turmerics" to 0.00043,
            "jerusalem artichoke" to 0.00043,
            "jerusalem artichokes" to 0.00043,
            "beetroot" to 0.00043,
            "beetroots" to 0.00043,
            "beet" to 0.00043,
            "beets" to 0.00043,
            "swede" to 0.00043,
            "swedes" to 0.00043,
            "rutabaga" to 0.00043,
            "rutabagas" to 0.00043,
            "ginger" to 0.00043,
            "gingers" to 0.00043,
            "garlic" to 0.00043,
            "garlics" to 0.00043,
            "jicama" to 0.00043,
            "jicamas" to 0.00043,
            "citrus fruit" to 0.00039,
            "citrus fruits" to 0.00039,
            "citrus" to 0.00039,
            "amanatsu" to 0.00039,
            "citron" to 0.00039,
            "citrons" to 0.00039,
            "orange" to 0.00039,
            "oranges" to 0.00039,
            "buddhas hand" to 0.00039,
            "buddhas hands" to 0.00039,
            "calamondin" to 0.00039,
            "calamondins" to 0.00039,
            "cam sành" to 0.00039,
            "citrange" to 0.00039,
            "citranges" to 0.00039,
            "citrumelo" to 0.00039,
            "citrumelos" to 0.00039,
            "clementine" to 0.00039,
            "clementines" to 0.00039,
            "lime" to 0.00039,
            "limes" to 0.00039,
            "etrog" to 0.00039,
            "ethrog" to 0.00039,
            "esrog" to 0.00039,
            "etrogim" to 0.00039,
            "ethrogim" to 0.00039,
            "esrogim" to 0.00039,
            "etrogs" to 0.00039,
            "ethrogs" to 0.00039,
            "esrogs" to 0.00039,
            "grapefruit" to 0.00039,
            "grapefruits" to 0.00039,
            "haruka" to 0.00039,
            "hassaku" to 0.00039,
            "hyuganatsu" to 0.00039,
            "jabara" to 0.00039,
            "kabosu" to 0.00039,
            "kanpei" to 0.00039,
            "kawachi bankan" to 0.00039,
            "kinkoji unshiu" to 0.00039,
            "kinnow" to 0.00039,
            "kiyomi" to 0.00039,
            "kobayashi mikan" to 0.00039,
            "kumquat" to 0.00039,
            "kumquats" to 0.00039,
            "lemon" to 0.00039,
            "lemons" to 0.00039,
            "lumia" to 0.00039,
            "lumias" to 0.00039,
            "mandarin" to 0.00039,
            "mandarins" to 0.00039,
            "mandarine" to 0.00039,
            "mandarines" to 0.00039,
            "mangshanyegan" to 0.00039,
            "orangelo" to 0.00039,
            "orangelos" to 0.00039,
            "oroblanco" to 0.00039,
            "oroblancos" to 0.00039,
            "oro blanco" to 0.00039,
            "pomelit" to 0.00039,
            "papeda" to 0.00039,
            "papedas" to 0.00039,
            "pomelo" to 0.00039,
            "pomelos" to 0.00039,
            "pompia" to 0.00039,
            "pumpia" to 0.00039,
            "ponkan" to 0.00039,
            "ponkans" to 0.00039,
            "rangpur" to 0.00039,
            "rangpurs" to 0.00039,
            "satsuma" to 0.00039,
            "satsumas" to 0.00039,
            "shangjuan" to 0.00039,
            "shonan gold" to 0.00039,
            "sudachi" to 0.00039,
            "tangerine" to 0.00039,
            "tangerines" to 0.00039,
            "tangelo" to 0.00039,
            "tangelos" to 0.00039,
            "yūkō" to 0.00039,
            "yukou" to 0.00039,
            "yuzu" to 0.00039,
            "yuzus" to 0.00039
        )
    }

    val TWO_WORD_INGREDIENTS: HashSet<String> by lazy {
        hashSetOf(
            "smoked",
            "short",
            "filet",
            "filets",
            "dark",
            "saewoo",
            "saeu",
            "grana",
            "parmigiano",
            "monterey",
            "spare",
            "jelly",
            "corn",
            "golden",
            "maple",
            "simple",
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
            "cherry",
            "beet",
            "vicia",
            "hoagie",
            "manihot",
            "custard",
            "sweet",
            "passion",
            "dragon",
            "mamey",
            "soy",
            "bamboo",
            "bell",
            "red",
            "yellow",
            "green",
            "banana",
            "lambs",
            "valerianella",
            "bitter",
            "brussels",
            "mustard",
            "collard",
            "bok",
            "spoon",
            "root",
            "jerusalem",
            "citrus",
            "buddhas",
            "cam",
            "kawachi",
            "kinkoji",
            "kobayashi",
            "oro",
            "shonan"
        )
    }

    val THREE_WORD_INGREDIENTS: HashSet<String> by lazy {
        hashSetOf(
            "baby",
            "half",
            "yuzus"
        )
    }
}
