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
        final ContentEntity rootContent = Mockito.mock(ContentEntity.class);
        final ContentDataEntity rootData = Mockito.mock(ContentDataEntity.class);
        final ContentEntity branchContent = Mockito.mock(ContentEntity.class);
        final ContentDataEntity branchData = Mockito.mock(ContentDataEntity.class);
        final ContentEntity leafContent = Mockito.mock(ContentEntity.class);
        final ContentDataEntity leafData = Mockito.mock(ContentDataEntity.class);

        Mockito.when(english.getLocale()).thenReturn("en");

        Mockito.when(leafContent.getContentParent()).thenReturn(branchContent);
        Mockito.when(branchContent.getContentParent()).thenReturn(rootContent);
        Mockito.when(rootContent.getContentParent()).thenReturn(null);

        Mockito.when(leafContent.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", leafData);}});
        Mockito.when(branchContent.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", branchData);}});
        Mockito.when(rootContent.getContentDataList()).thenReturn(new HashMap<String, ContentDataEntity>(){{put("en", rootData);}});

        Mockito.when(leafData.getSlug()).thenReturn("//leaf");
        Mockito.when(leafData.getLanguage()).thenReturn(english);
        Mockito.when(branchData.getSlug()).thenReturn("branch");
        Mockito.when(branchData.getLanguage()).thenReturn(english);
        Mockito.when(rootData.getSlug()).thenReturn("/root/");
        Mockito.when(rootData.getLanguage()).thenReturn(english);

        final String rootComputedSlug = CmsUtils.computeSlug(rootContent, rootData, "en");
        Assert.assertEquals("/root", rootComputedSlug);

        Mockito.when(rootData.getComputedSlug()).thenReturn(rootComputedSlug);

        final String branchComputedSlug = CmsUtils.computeSlug(branchContent, branchData, "en");
        Assert.assertEquals("/root/branch", branchComputedSlug);

        Mockito.when(branchData.getComputedSlug()).thenReturn(branchComputedSlug);

        final String leafComputedSlug = CmsUtils.computeSlug(leafContent, leafData, "en");
        Assert.assertEquals("/root/branch/leaf", leafComputedSlug);
    }

}
