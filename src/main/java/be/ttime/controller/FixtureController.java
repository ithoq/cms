package be.ttime.controller;

import be.ttime.core.persistence.model.*;
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
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class FixtureController {

    @Autowired
    private IPageService pageService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private IPageBlockService pageBlockService;
    @Autowired
    private IAuthorityService authorityService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IPageTemplateService pageTemplateService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private IMessageService messageService;

    @RequestMapping(value = "/install", method = RequestMethod.GET)
    @ResponseBody
    public String dbinsert(ModelMap model, HttpServletRequest request) {


        PageBlockEntity entity = pageBlockService.findByNameAndBlockType("master", PageBlockEntity.BlockType.System);

        if (entity != null) {
            return "The installation was already done!";
        }

        ApplicationLanguageEntity appLanguageEN = new ApplicationLanguageEntity();
        appLanguageEN.setEnabledForAdmin(true);
        appLanguageEN.setEnabledForPublic(true);
        appLanguageEN.setRtl(false);
        appLanguageEN.setLocale("en_US");

        ApplicationLanguageEntity appLanguageFR = new ApplicationLanguageEntity();
        appLanguageFR.setEnabledForAdmin(true);
        appLanguageFR.setEnabledForPublic(false);
        appLanguageFR.setRtl(false);
        appLanguageFR.setLocale("fr_FR");

        applicationService.saveApplicationLanguage(appLanguageEN);
        applicationService.saveApplicationLanguage(appLanguageFR);

        ApplicationConfigEntity appConfig = new ApplicationConfigEntity();
        appConfig.setForcedLangInUrl(false);
        appConfig.setIsoTwoLetter(true);
        appConfig.setDefaultAdminLang(appLanguageEN);
        appConfig.setDefaultPublicLang(appLanguageFR);

        applicationService.saveApplicationConfig(appConfig);

        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setEmail("user@email.com");
        user.setCreatedDate(new Date());
        user.setPassword(bCryptPasswordEncoder.encode("pass"));

        // on ajoute un utilisateur

        // == create initial privileges
        final PrivilegeEntity readPrivilege = authorityService.createPrivilegeIfNotFound("READ_PRIVILEGE");
        final PrivilegeEntity writePrivilege = authorityService.createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        // == create initial roles
        final List<PrivilegeEntity> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);

        authorityService.createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        authorityService.createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));

        final RoleEntity adminRole = authorityService.findRoleByName("ROLE_ADMIN");
        user.setRoles(Arrays.asList(adminRole));

        userService.save(user);

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

        List<MessageEntity> msgs = new ArrayList<>();
        MessageEntity m;
        List<MessageTranslationsEntity> list;

        // admin messages
        m = new MessageEntity("admin.dropFilesHere", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "déposez vos fichiers ici", m), new MessageTranslationsEntity(appLanguageEN, "drop files here", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.handCrafted", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "à la main", m), new MessageTranslationsEntity(appLanguageEN, "hand-crafted", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.madeWithLove", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "conçu avec amour", m), new MessageTranslationsEntity(appLanguageEN, "made with love", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.blocks", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "blocs", m), new MessageTranslationsEntity(appLanguageEN, "blocks", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.blockEdit", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "éditier un bloc", m), new MessageTranslationsEntity(appLanguageEN, "edit block", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.blocksManagement", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "gestion des blocs", m), new MessageTranslationsEntity(appLanguageEN, "blocks manager", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.newBlock", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "nouveau bloc", m), new MessageTranslationsEntity(appLanguageEN, "new block", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.newPage", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "nouvelle page", m), new MessageTranslationsEntity(appLanguageEN, "new page", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pages", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "pages", m), new MessageTranslationsEntity(appLanguageEN, "pages", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pagesManagement", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "gestion des pages", m), new MessageTranslationsEntity(appLanguageEN, "pages manager", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pagesList", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "liste des pages", m), new MessageTranslationsEntity(appLanguageEN, "pages list", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pageDeleteMessage", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "cette page va être supprimée", m), new MessageTranslationsEntity(appLanguageEN, "this page will be deleted", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pageEdit", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "éditer une page", m), new MessageTranslationsEntity(appLanguageEN, "edit page", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pageParam", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "paramètres de la page", m), new MessageTranslationsEntity(appLanguageEN, "page parameters", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pageTemplate", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "modèle de page", m), new MessageTranslationsEntity(appLanguageEN, "page template", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.pageTitle", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "titre de la page", m), new MessageTranslationsEntity(appLanguageEN, "page title", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.rootPage", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "page racine", m), new MessageTranslationsEntity(appLanguageEN, "root page", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("admin.tree", "admin");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "arborescence", m), new MessageTranslationsEntity(appLanguageEN, "tree", m));
        m.setTranslations(list);
        msgs.add(m);


        // error messages
        m = new MessageEntity("error.general", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "une erreur s'est produite, veuillez réessayer plus tard", m), new MessageTranslationsEntity(appLanguageEN, "oops! Something went wrong. Please try again later", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("error.notFound", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "L'URL demandée n'a pas été trouvée sur le serveur", m), new MessageTranslationsEntity(appLanguageEN, "The requested url was not found on this server", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("error.auth.userNotFound", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "Utilisateur non trouvé", m), new MessageTranslationsEntity(appLanguageEN, "User Not Found", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("error.auth.userExist", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "Un compte pour ce nom d'utilisateur / email existe déjà. Entrez un nom d'utilisateur différent", m), new MessageTranslationsEntity(appLanguageEN, "An account for that username/email already exists. Please enter a different username", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("error.auth.invalidOldPassword", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "L'ancien mot de passe est incorrect", m), new MessageTranslationsEntity(appLanguageEN, "Invalid Old Password", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("error.mail.config", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "Erreur dans la configuration de JavaMail", m), new MessageTranslationsEntity(appLanguageEN, "Error in java mail configuration", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("auth.message.disabled", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "Votre compte est désactivé, merci de vérifier votre courrier et cliquez sur le lien de confirmation", m), new MessageTranslationsEntity(appLanguageEN, "Your account is disabled please check your mail and click on the confirmation link", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("auth.message.expired", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "Votre jeton d'inscription a expiré. Merci de vous réinscrire", m), new MessageTranslationsEntity(appLanguageEN, "Your registration token has expired. Please register again.", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("auth.message.invalidUser", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "Ce nom d'utilisateur est valide, ou n'existe pas", m), new MessageTranslationsEntity(appLanguageEN, "This username is invalid, or does not exist", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("auth.message.invalidToken", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "compte non valide jeton de confirmation", m), new MessageTranslationsEntity(appLanguageEN, "Invalid account confirmation token", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("auth.message.blocked", "error");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "Cette ip est bloqué pendant 24 heures", m), new MessageTranslationsEntity(appLanguageEN, "This ip is blocked for 24 hours", m));
        m.setTranslations(list);
        msgs.add(m);

        // success messages
        m = new MessageEntity("success.general", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "opération réussie", m), new MessageTranslationsEntity(appLanguageEN, "operation performed successfully", m));
        m.setTranslations(list);
        msgs.add(m);

        // general messages
        m = new MessageEntity("active", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "actif", m), new MessageTranslationsEntity(appLanguageEN, "active", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("administration", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "administration", m), new MessageTranslationsEntity(appLanguageEN, "administration", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("add", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "ajouter", m), new MessageTranslationsEntity(appLanguageEN, "add", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("allRightsReserved", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "tous droits réservés", m), new MessageTranslationsEntity(appLanguageEN, "all rights reserved", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("cacheable", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "en cache", m), new MessageTranslationsEntity(appLanguageEN, "cacheable", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("cancel", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "annuler", m), new MessageTranslationsEntity(appLanguageEN, "cancel", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("confirm", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "confirmer", m), new MessageTranslationsEntity(appLanguageEN, "confirm", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("content", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "contenu", m), new MessageTranslationsEntity(appLanguageEN, "content", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("delete", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "supprimer", m), new MessageTranslationsEntity(appLanguageEN, "delete", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("description", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "description", m), new MessageTranslationsEntity(appLanguageEN, "description", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("dynamic", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "dynamique", m), new MessageTranslationsEntity(appLanguageEN, "dynamic", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("edit", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "éditer", m), new MessageTranslationsEntity(appLanguageEN, "edit", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("enabled", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "activé", m), new MessageTranslationsEntity(appLanguageEN, "enabled", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("fieldset", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "groupe de champs", m), new MessageTranslationsEntity(appLanguageEN, "fieldset", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("file", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "fichier", m), new MessageTranslationsEntity(appLanguageEN, "file", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("general", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "général", m), new MessageTranslationsEntity(appLanguageEN, "general", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("level", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "niveau", m), new MessageTranslationsEntity(appLanguageEN, "level", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("link", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "lien", m), new MessageTranslationsEntity(appLanguageEN, "link", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("loading", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "chargement", m), new MessageTranslationsEntity(appLanguageEN, "loading", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("login", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "connexion", m), new MessageTranslationsEntity(appLanguageEN, "login", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("logout", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "déconnexion", m), new MessageTranslationsEntity(appLanguageEN, "logout", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("main", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "principal", m), new MessageTranslationsEntity(appLanguageEN, "main", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("menu", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "menu", m), new MessageTranslationsEntity(appLanguageEN, "menu", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("name", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "nom", m), new MessageTranslationsEntity(appLanguageEN, "name", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("navigation", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "navigation", m), new MessageTranslationsEntity(appLanguageEN, "navigation", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("no", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "non", m), new MessageTranslationsEntity(appLanguageEN, "no", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("operation", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "opération", m), new MessageTranslationsEntity(appLanguageEN, "operation", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("page", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "page", m), new MessageTranslationsEntity(appLanguageEN, "page", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("results", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "résultat(s)", m), new MessageTranslationsEntity(appLanguageEN, "result(s)", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("save", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "enregistrer", m), new MessageTranslationsEntity(appLanguageEN, "save", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("settings", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "options", m), new MessageTranslationsEntity(appLanguageEN, "settings", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("search", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "recherche", m), new MessageTranslationsEntity(appLanguageEN, "search", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("size", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "taille", m), new MessageTranslationsEntity(appLanguageEN, "size", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("static", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "statique", m), new MessageTranslationsEntity(appLanguageEN, "static", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("template", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "modèle", m), new MessageTranslationsEntity(appLanguageEN, "template", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("warning", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "attention", m), new MessageTranslationsEntity(appLanguageEN, "warning", m));
        m.setTranslations(list);
        msgs.add(m);

        m = new MessageEntity("yes", "general");
        list = Arrays.asList(new MessageTranslationsEntity(appLanguageFR, "oui", m), new MessageTranslationsEntity(appLanguageEN, "yes", m));
        m.setTranslations(list);
        msgs.add(m);


        messageService.save(msgs);

        return "The data has been added successfully!";
    }
}