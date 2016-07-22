package be.ttime.core.util;

import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.HashMap;

@RunWith(JUnit4.class)
public class CmsUtilsTest {

    @Test
    public void testComputeSlug() throws Exception {

        final ApplicationLanguageEntity english = Mockito.mock(ApplicationLanguageEntity.class);
        final ContentEntity homeContent = Mockito.mock(ContentEntity.class);
        final ContentDataEntity homeData = Mockito.mock(ContentDataEntity.class);
        final ContentEntity rootContent = Mockito.mock(ContentEntity.class);
        final ContentDataEntity rootData = Mockito.mock(ContentDataEntity.class);
        final ContentEntity branchContent = Mockito.mock(ContentEntity.class);
        final ContentDataEntity branchData = Mockito.mock(ContentDataEntity.class);
        final ContentEntity leafContent = Mockito.mock(ContentEntity.class);
        final ContentDataEntity leafData = Mockito.mock(ContentDataEntity.class);

        final ContentEntity rootContentWithLocale  = Mockito.mock(ContentEntity.class);
        final ContentDataEntity rootDataWithLocale = Mockito.mock(ContentDataEntity.class);

        Mockito.when(english.getLocale()).thenReturn("en");

        Mockito.when(leafContent.getContentParent()).thenReturn(branchContent);
        Mockito.when(branchContent.getContentParent()).thenReturn(rootContent);
        Mockito.when(rootContent.getContentParent()).thenReturn(null);

        Mockito.when(homeContent.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", homeData);}});
        Mockito.when(leafContent.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", leafData);}});
        Mockito.when(branchContent.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", branchData);}});
        Mockito.when(rootContent.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", rootData);}});
        Mockito.when(rootContentWithLocale.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", rootDataWithLocale);}});

        Mockito.when(homeData.getSlug()).thenReturn("/");
        Mockito.when(homeData.getLanguage()).thenReturn(english);
        Mockito.when(leafData.getSlug()).thenReturn("//leaf");
        Mockito.when(leafData.getLanguage()).thenReturn(english);
        Mockito.when(branchData.getSlug()).thenReturn("branch");
        Mockito.when(branchData.getLanguage()).thenReturn(english);
        Mockito.when(rootData.getSlug()).thenReturn("/root/");
        Mockito.when(rootData.getLanguage()).thenReturn(english);
        Mockito.when(rootDataWithLocale.getSlug()).thenReturn("/root");
        Mockito.when(rootDataWithLocale.getLanguage()).thenReturn(english);

        // Test root
        final String homeComputedSlug = CmsUtils.computeSlug(homeContent, homeData, "en", false);
        Assert.assertEquals("/", homeComputedSlug);

        // Test root with locale
        final String homeComputedSlugWithLocale = CmsUtils.computeSlug(homeContent, homeData, "en", true);
        Assert.assertEquals("/en", homeComputedSlugWithLocale);

        // Test parent
        final String rootComputedSlug = CmsUtils.computeSlug(rootContent, rootData, "en", false);
        Assert.assertEquals("/root", rootComputedSlug);

        // Test branch
        Mockito.when(rootData.getComputedSlug()).thenReturn(rootComputedSlug);
        final String branchComputedSlug = CmsUtils.computeSlug(branchContent, branchData, "en", false);
        Assert.assertEquals("/root/branch", branchComputedSlug);

        // Test leaf
        Mockito.when(branchData.getComputedSlug()).thenReturn(branchComputedSlug);
        final String leafComputedSlug = CmsUtils.computeSlug(leafContent, leafData, "en", false);
        Assert.assertEquals("/root/branch/leaf", leafComputedSlug);

        // Test with locale
        final String localeComputedSlug = CmsUtils.computeSlug(rootContentWithLocale, rootDataWithLocale, "en", true);
        Assert.assertEquals("/en/root", localeComputedSlug);

        // Test with bad slug
        Mockito.when(rootDataWithLocale.getSlug()).thenReturn("root");
        final String localeComputedSlug2 = CmsUtils.computeSlug(rootContentWithLocale, rootDataWithLocale, "en", true);
        Assert.assertEquals("/en/root", localeComputedSlug);

    }


}
