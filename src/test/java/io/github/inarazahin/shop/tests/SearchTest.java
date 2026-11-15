package io.github.inarazahin.shop.tests;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.inarazahin.shop.pages.HomePage;
import io.github.inarazahin.shop.pages.ProductPage;
import io.github.inarazahin.shop.pages.SearchResultsPage;
import io.qameta.allure.Feature;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * The demo catalogue is fixed, so search results are predictable. Note that PrestaShop's
 * search is fuzzy: even nonsense terms return "similar" products, so there is deliberately
 * no "no results" test - it would assert behaviour the shop does not have.
 */
@Feature("Search")
public class SearchTest extends BaseTest {

    @Test(groups = SMOKE, description = "Searching from the header shows matching products")
    public void searchFromHeader() {
        SearchResultsPage results = new HomePage(driver()).open().header().search("mug");

        assertThat(results.productNames())
                .isNotEmpty()
                .allSatisfy(name -> assertThat(name).containsIgnoringCase("mug"));
    }

    @DataProvider(name = "terms", parallel = true)
    public Object[][] terms() {
        return new Object[][] {{"t-shirt"}, {"poster"}, {"notebook"}};
    }

    @Test(groups = REGRESSION, dataProvider = "terms",
            description = "Every result is relevant and the stated count matches the list")
    public void resultsAreRelevantAndCounted(String term) {
        SearchResultsPage results = new SearchResultsPage(driver()).open(term);

        List<String> names = results.productNames();
        assertThat(names).isNotEmpty().allSatisfy(name -> assertThat(name).containsIgnoringCase(term));
        assertThat(results.statedTotal()).isEqualTo(names.size());
    }

    @Test(groups = REGRESSION, description = "Search is case-insensitive")
    public void searchIsCaseInsensitive() {
        List<String> lower = new SearchResultsPage(driver()).open("mug").productNames();
        List<String> upper = new SearchResultsPage(driver()).open("MUG").productNames();

        assertThat(upper).containsExactlyInAnyOrderElementsOf(lower);
    }

    @Test(groups = REGRESSION, description = "A result opens the matching product page")
    public void openProductFromResults() {
        SearchResultsPage results = new SearchResultsPage(driver()).open("hummingbird");

        ProductPage product = results.openProduct("Hummingbird printed t-shirt");

        assertThat(product.name()).isEqualToIgnoringCase("Hummingbird printed t-shirt");
        assertThat(product.price()).isPositive();
    }
}
