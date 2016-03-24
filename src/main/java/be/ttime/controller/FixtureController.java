package be.ttime.controller;

import be.ttime.core.persistence.dao.*;
import be.ttime.core.persistence.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class FixtureController {

    @Autowired
    private IPageService pageService;
    @Autowired
    private IPageBlockService pageBlockService;
    @Autowired
    private IUserService pageUser;
    @Autowired
    private IPageTemplateService pageTemplateService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ILocalizedMessageService localizedMessageService;

    @RequestMapping(value = "/install", method = RequestMethod.GET)
    @ResponseBody
    public String dbinsert(ModelMap model, HttpServletRequest request) {


        PageBlockEntity entity = pageBlockService.findByNameAndBlockType("master", PageBlockEntity.BlockType.System);

        if (entity != null) {
            return "The installation was already done!";
        }

        UserEntity fab = new UserEntity();
        fab.setUsername("user");
        fab.setPassword(bCryptPasswordEncoder.encode("pass"));

        // on ajoute un utilisateur
        AuthorityEntity authority_user = new AuthorityEntity();
        authority_user.setAuthority("ROLE_USER");
        AuthorityEntity authority_admin = new AuthorityEntity();
        authority_admin.setAuthority("ROLE_ADMIN");
        Set<AuthorityEntity> authorities = new HashSet<>();
        authorities.add(authority_user);
        authorities.add(authority_admin);

        fab.setAuthorities(authorities);

        pageUser.save(fab);

        PageBlockEntity pageTemplateContent = new PageBlockEntity();
        pageTemplateContent.setName("template1");
        pageTemplateContent.setDynamic(true);
        pageTemplateContent.setCacheable(false);
        pageTemplateContent.setContent("<div class=\"Template\"><h1>testTemplate</h1><h2>content</h2>{{data.mainContent_tiny | raw}}<h2>youtube link</h2>{{data.yt_txt}}</div>");
        pageTemplateContent.setBlockType(PageBlockEntity.BlockType.PageTemplate);
        pageBlockService.save(pageTemplateContent);

        PageTemplateEntity pTemplate = new PageTemplateEntity();
        pTemplate.setName("video");
        pTemplate.setFields("[ { \"namespace\":\"mainContent\", \"name\":\"Main Content\", \"description\":\"\", \"blockName\":\"tinymce\", \"inputs\":[ { \"title\":\"Main Content\", \"subtitle\":\"This is the main content\", \"name\":\"tiny\", \"required\":false, \"validation\":\"\", \"defaultValue\":\"\", \"hint\":\"\", \"type\":\"\" } ] }, { \"namespace\":\"yt\", \"name\":\"Youtube Link\", \"description\":\"desc\", \"blockName\":\"text\", \"inputs\":[ { \"title\":\"Youtube Link\", \"subtitle\":\"\", \"name\":\"txt\", \"required\":true, \"validation\":\"\", \"defaultValue\":\"empty!\", \"hint\":\"Indique le contenu\", \"type\":\"\" } ] } ]");
        pTemplate.setPageBlock(pageTemplateContent);
        pageTemplateService.save(pTemplate);

        PageEntity p1 = new PageEntity();
        p1.setCreatedDate(new Date());
        p1.setName("Home");
        p1.setPageTemplate(pTemplate);
        p1.setLevel(0);
        p1.setOrder(0);
        p1.setSlug("/");

        pageService.save(p1);

        PageBlockEntity fieldText = new PageBlockEntity();
        fieldText.setName("text");
        fieldText.setDynamic(true);
        fieldText.setContent("{% set fieldName = np + '_' + input.name %}\n" +
                "<div class=\"form-group form-group-default required\">\n" +
                "    <label>{{input.title}}</label>{% if input.hint is not empty %} <i data-placement=\"right\" title=\"\" data-toggle=\"tooltip\" data-original-title=\"{{input.hint}}\" class=\"fa fa-info-circle txt-transform-reset\"></i>{% endif %}\n" +
                "    <input type=\"text\" class=\"form-control\" name=\"{{fieldName}}\" id=\"{{fieldName}}\" value=\"{{data[fieldName]}}\">\n" +
                "</div>"
        );
        fieldText.setBlockType(PageBlockEntity.BlockType.FieldSet);
        fieldText.setDeletable(false);

        PageBlockEntity fieldTinyMce = new PageBlockEntity();
        fieldTinyMce.setName("tinymce");
        fieldTinyMce.setDynamic(true);
        fieldTinyMce.setContent("{% set fieldName = np + '_' + input.name %}\n" +
                "<h3 class=\"like-label\">{{input.title}}</h3>{% if input.hint is not empty %} <i data-placement=\"right\" title=\"\" data-toggle=\"tooltip\" data-original-title=\"{{input.hint}}\" class=\"fa fa-info-circle txt-transform-reset\"></i>{% endif %}\n" +
                "{% if input.subtitle is not empty %}\n" +
                "<p>{{input.subtitle}}</p>\n" +
                "{% endif %}\n" +
                "<div class=\"form-group\">\n" +
                "    <textarea data-editor=\"tinymce\" class=\"tinymce\" id=\"{{fieldName}}\" name=\"{{fieldName}}\">{{data[fieldName]}}</textarea>\n" +
                "</div>\n");
        fieldTinyMce.setBlockType(PageBlockEntity.BlockType.FieldSet);
        fieldTinyMce.setDeletable(false);

        PageBlockEntity masterTemplate = new PageBlockEntity();
        masterTemplate.setName("master");
        masterTemplate.setContent("<!DOCTYPE html>\n" +
                "<html\n" +
                "<head>\n" +
                "    <title>{% if title is not empty %}{{title}}{% else %}Default title{% endif %}</title>\n" +
                "    <meta names=\"description\" content=\"{% if description is not empty %}{{description}}{% else %}Default Description{% endif %}\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta names=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1\">\n" +
                "    {{ include_top }}\n" +
                "</head>\n" +
                "<body>\n" +
                "    \n" +
                "    {{ main | raw }}\n" +
                "\n" +
                "    <script type=\"text/javascript\" src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js\"></script>\n" +
                "    {{ include_bottom }}\n" +
                "</body>\n" +
                "</html>");
        masterTemplate.setDynamic(true);
        masterTemplate.setDeletable(false);
        masterTemplate.setCacheable(false);
        masterTemplate.setBlockType(PageBlockEntity.BlockType.System);

        PageBlockEntity loginPage = new PageBlockEntity();
        loginPage.setContent("<div class=\"l-container\">\n" +
                "    <div class=\"row\">\n" +
                "        <div class=\"col-md-12\">\n" +
                "            <div class=\"content-container\">\n" +
                "                <div class=\"row\">\n" +
                "                    <div class=\"col-md-12\">\n" +
                "                        <form action=\"/login\" method=\"post\">\n" +
                "                            <fieldset>\n" +
                "                                <legend>Please Login</legend>\n" +
                "                                <!-- use param.error assuming FormLoginConfigurer#failureUrl contains the query parameter error -->\n" +
                "                                {% if get.error is not null %}\n" +
                "                                    <div>\n" +
                "                                        Failed to login.\n" +
                "                                        Reason: {{session.getAttribute(\"SPRING_SECURITY_LAST_EXCEPTION\").message}}\n" +
                "                                    </div>\n" +
                "                                {% endif %}\n" +
                "                                <!-- the configured LogoutConfigurer#logoutSuccessUrl is /login?logout and contains the query param logout -->\n" +
                "                                {% if get.logout is not null %}\n" +
                "                                    <div>\n" +
                "                                        You have been logged out.\n" +
                "                                    </div>\n" +
                "                                {% endif %}\n" +
                "                                <p>\n" +
                "                                    <label for=\"username\">Username</label>\n" +
                "                                    <input type=\"text\" id=\"username\" name=\"username\"/>\n" +
                "                                </p>\n" +
                "                                <p>\n" +
                "                                    <label for=\"password\">Password</label>\n" +
                "                                    <input type=\"password\" id=\"password\" name=\"password\"/>\n" +
                "                                </p>\n" +
                "                                <!-- if using RememberMeConfigurer make sure remember-me matches RememberMeConfigurer#rememberMeParameter -->\n" +
                "                                <p>\n" +
                "                                    <label for=\"remember-me\">Remember Me?</label>\n" +
                "                                    <input type=\"checkbox\" id=\"remember-me\" name=\"remember-me\"/>\n" +
                "                                </p>\n" +
                "                                <div>\n" +
                "                                    <button type=\"submit\" class=\"btn\">Log in</button>\n" +
                "                                </div>\n" +
                "                                {{ csrf | raw }}\n" +
                "                            </fieldset>\n" +
                "                        </form>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n");
        loginPage.setName("login");
        loginPage.setDynamic(true);
        loginPage.setDeletable(false);
        loginPage.setCacheable(false);
        loginPage.setBlockType(PageBlockEntity.BlockType.System);

        pageBlockService.save(fieldText);
        pageBlockService.save(fieldTinyMce);
        pageBlockService.save(masterTemplate);
        pageBlockService.save(loginPage);

        List<LocalizedMessageEntity> msgs = new ArrayList<>();

        // admin messages
        msgs.add(new LocalizedMessageEntity(0, "admin.dropFilesHere", "admin", "drop files here", "déposez vos fichiers ici"));
        msgs.add(new LocalizedMessageEntity(0, "admin.handCrafted", "admin", "hand-crafted", "à la main"));
        msgs.add(new LocalizedMessageEntity(0, "admin.madeWithLove", "admin", "made with love", "conçu avec amour"));
        msgs.add(new LocalizedMessageEntity(0, "admin.blocks", "admin", "blocks", "blocs"));
        msgs.add(new LocalizedMessageEntity(0, "admin.blockEdit", "admin", "edit block", "éditier un bloc"));
        msgs.add(new LocalizedMessageEntity(0, "admin.blocksManagement", "admin", "blocks manager", "gestion des blocs"));
        msgs.add(new LocalizedMessageEntity(0, "admin.newBlock", "admin", "new block", "nouveau bloc"));
        msgs.add(new LocalizedMessageEntity(0, "admin.newPage", "admin", "new page", "nouvelle page"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pages", "admin", "pages", "pages"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pagesManagement", "admin", "pages manager", "gestion des pages"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pagesList", "admin", "pages list", "liste des pages"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pageDeleteMessage", "admin", "this page will be deleted", "cette page va être supprimée"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pageEdit", "admin", "edit page", "éditer une page"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pageParam", "admin", "page parameters", "paramètres de la page"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pageTemplate", "admin", "page template", "modèle de page"));
        msgs.add(new LocalizedMessageEntity(0, "admin.pageTitle", "admin", "page title", "titre de la page"));
        msgs.add(new LocalizedMessageEntity(0, "admin.rootPage", "admin", "root page", "page racine"));
        msgs.add(new LocalizedMessageEntity(0, "admin.tree", "admin", "tree", "arborescence"));

        // general message
        msgs.add(new LocalizedMessageEntity(0, "active", "general", "active", "actif"));
        msgs.add(new LocalizedMessageEntity(0, "administration", "general", "administration", "administration"));
        msgs.add(new LocalizedMessageEntity(0, "add", "general", "add", "ajouter"));
        msgs.add(new LocalizedMessageEntity(0, "allRightsReserved", "general", "all rights reserved", "tous droits réservés"));
        msgs.add(new LocalizedMessageEntity(0, "cacheable", "general", "cacheable", "en cache"));
        msgs.add(new LocalizedMessageEntity(0, "cancel", "general", "cancel", "annuler"));
        msgs.add(new LocalizedMessageEntity(0, "confirm", "general", "confirm", "confirmer"));
        msgs.add(new LocalizedMessageEntity(0, "content", "general", "content", "contenu"));
        msgs.add(new LocalizedMessageEntity(0, "delete", "general", "delete", "supprimer"));
        msgs.add(new LocalizedMessageEntity(0, "description", "general", "description", "déscription"));
        msgs.add(new LocalizedMessageEntity(0, "dynamic", "general", "dynamic", "dynamique"));
        msgs.add(new LocalizedMessageEntity(0, "edit", "general", "edit", "éditer"));
        msgs.add(new LocalizedMessageEntity(0, "enabled", "general", "enabled", "activé"));
        msgs.add(new LocalizedMessageEntity(0, "fieldset", "general", "fieldset", "groupe de champs"));
        msgs.add(new LocalizedMessageEntity(0, "file", "general", "file", "fichier"));
        msgs.add(new LocalizedMessageEntity(0, "general", "general", "general", "général"));
        msgs.add(new LocalizedMessageEntity(0, "genericSuccess", "general", "operation performed successfully", "opération réussie"));
        msgs.add(new LocalizedMessageEntity(0, "genericError", "general", "oops! Something went wrong. Please try again later", "une erreur s'est produite, veuillez réessayer plus tard"));
        msgs.add(new LocalizedMessageEntity(0, "level", "general", "level", "niveau"));
        msgs.add(new LocalizedMessageEntity(0, "link", "general", "link", "lien"));
        msgs.add(new LocalizedMessageEntity(0, "loading", "general", "loading", "chargement"));
        msgs.add(new LocalizedMessageEntity(0, "login", "general", "login", "connexion"));
        msgs.add(new LocalizedMessageEntity(0, "logout", "general", "logout", "déconnexion"));
        msgs.add(new LocalizedMessageEntity(0, "main", "general", "main", "principal"));
        msgs.add(new LocalizedMessageEntity(0, "menu", "general", "menu", "déconnexion"));
        msgs.add(new LocalizedMessageEntity(0, "name", "general", "name", "nom"));
        msgs.add(new LocalizedMessageEntity(0, "navigation", "general", "navigation", "navigation"));
        msgs.add(new LocalizedMessageEntity(0, "no", "general", "no", "non"));
        msgs.add(new LocalizedMessageEntity(0, "operation", "general", "operation", "opération"));
        msgs.add(new LocalizedMessageEntity(0, "page", "general", "page", "page"));
        msgs.add(new LocalizedMessageEntity(0, "results", "general", "result(s)", "résultat(s)"));
        msgs.add(new LocalizedMessageEntity(0, "save", "general", "save", "enregistrer"));
        msgs.add(new LocalizedMessageEntity(0, "settings", "general", "settings", "options"));
        msgs.add(new LocalizedMessageEntity(0, "search", "general", "search", "recherche"));
        msgs.add(new LocalizedMessageEntity(0, "size", "general", "size", "taille"));
        msgs.add(new LocalizedMessageEntity(0, "static", "general", "static", "statique"));
        msgs.add(new LocalizedMessageEntity(0, "template", "general", "template", "modèle"));
        msgs.add(new LocalizedMessageEntity(0, "warning", "general", "warning", "attention"));
        msgs.add(new LocalizedMessageEntity(0, "yes", "general", "yes", "oui"));

        localizedMessageService.save(msgs);
        return "The data has been added successfully!";
    }
}
