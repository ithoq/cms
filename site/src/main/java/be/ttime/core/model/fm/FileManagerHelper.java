package be.ttime.core.model.fm;


import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class FileManagerHelper {

    public static Map<String,String> mimetypes;

    static {
        mimetypes = new HashMap<>();

        mimetypes.put("3dml","text/vnd.in3d.3dml");
        mimetypes.put("3g2","video/3gpp2");
        mimetypes.put("3gp","video/3gpp");
        mimetypes.put("7z","application/x-7z-compressed");
        mimetypes.put("aab","application/x-authorware-bin");
        mimetypes.put("aac","audio/x-aac");
        mimetypes.put("aam","application/x-authorware-map");
        mimetypes.put("aas","application/x-authorware-seg");
        mimetypes.put("abw","application/x-abiword");
        mimetypes.put("ac","application/pkix-attr-cert");
        mimetypes.put("acc","application/vnd.americandynamics.acc");
        mimetypes.put("ace","application/x-ace-compressed");
        mimetypes.put("acu","application/vnd.acucobol");
        mimetypes.put("adp","audio/adpcm");
        mimetypes.put("aep","application/vnd.audiograph");
        mimetypes.put("afp","application/vnd.ibm.modcap");
        mimetypes.put("ahead","application/vnd.ahead.space");
        mimetypes.put("ai","application/postscript");
        mimetypes.put("aif","audio/x-aiff");
        mimetypes.put("air","application/vnd.adobe.air-application-installer-package+zip");
        mimetypes.put("ait","application/vnd.dvb.ait");
        mimetypes.put("ami","application/vnd.amiga.ami");
        mimetypes.put("apk","application/vnd.android.package-archive");
        mimetypes.put("application","application/x-ms-application");
        mimetypes.put("apr","application/vnd.lotus-approach");
        mimetypes.put("asf","video/x-ms-asf");
        mimetypes.put("aso","application/vnd.accpac.simply.aso");
        mimetypes.put("atc","application/vnd.acucorp");
        mimetypes.put("atom","application/atom+xml");
        mimetypes.put("atomcat", "application/atomcat+xml");
        mimetypes.put("atomsvc","application/atomsvc+xml");
        mimetypes.put("atx","application/vnd.antix.game-component");
        mimetypes.put("au","audio/basic");
        mimetypes.put("avi","video/x-msvideo");
        mimetypes.put("aw","application/applixware");
        mimetypes.put("azf","application/vnd.airzip.filesecure.azf");
        mimetypes.put("azs","application/vnd.airzip.filesecure.azs");
        mimetypes.put("azw","application/vnd.amazon.ebook");
        mimetypes.put("bcpio","application/x-bcpio");
        mimetypes.put("bdf","application/x-font-bdf");
        mimetypes.put("bdm","application/vnd.syncml.dm+wbxml");
        mimetypes.put("bed","application/vnd.realvnc.bed");
        mimetypes.put("bh2","application/vnd.fujitsu.oasysprs");
        mimetypes.put("bin","application/octet-stream");
        mimetypes.put("bmi","application/vnd.bmi");
        mimetypes.put("bmp","image/bmp");
        mimetypes.put("box","application/vnd.previewsystems.box");
        mimetypes.put("btif","image/prs.btif");
        mimetypes.put("bz","application/x-bzip");
        mimetypes.put("bz2","application/x-bzip2");
        mimetypes.put("c","text/x-c");
        mimetypes.put("c11amc","application/vnd.cluetrust.cartomobile-config");
        mimetypes.put("c11amz","application/vnd.cluetrust.cartomobile-config-pkg");
        mimetypes.put("c4g","application/vnd.clonk.c4group");
        mimetypes.put("cab","application/vnd.ms-cab-compressed");
        mimetypes.put("car","application/vnd.curl.car");
        mimetypes.put("cat","application/vnd.ms-pki.seccat");
        mimetypes.put("ccxml","application/ccxml+xml,");
        mimetypes.put("cdbcmsg","application/vnd.contact.cmsg");
        mimetypes.put("cdkey","application/vnd.mediastation.cdkey");
        mimetypes.put("cdmia","application/cdmi-capability");
        mimetypes.put("cdmic","application/cdmi-container");
        mimetypes.put("cdmid","application/cdmi-domain");
        mimetypes.put("cdmio","application/cdmi-object");
        mimetypes.put("cdmiq","application/cdmi-queue");
        mimetypes.put("cdx","chemical/x-cdx");
        mimetypes.put("cdxml","application/vnd.chemdraw+xml");
        mimetypes.put("cdy","application/vnd.cinderella");
        mimetypes.put("cer","application/pkix-cert");
        mimetypes.put("cgm","image/cgm");
        mimetypes.put("chat","application/x-chat");
        mimetypes.put("chm","application/vnd.ms-htmlhelp");
        mimetypes.put("chrt","application/vnd.kde.kchart");
        mimetypes.put("cif","chemical/x-cif");
        mimetypes.put("cii","application/vnd.anser-web-certificate-issue-initiation");
        mimetypes.put("cil","application/vnd.ms-artgalry");
        mimetypes.put("cla","application/vnd.claymore");
        mimetypes.put("class","application/java-vm");
        mimetypes.put("clkk","application/vnd.crick.clicker.keyboard");
        mimetypes.put("clkp","application/vnd.crick.clicker.palette");
        mimetypes.put("clkt","application/vnd.crick.clicker.template");
        mimetypes.put("clkw","application/vnd.crick.clicker.wordbank");
        mimetypes.put("clkx","application/vnd.crick.clicker");
        mimetypes.put("clp","application/x-msclip");
        mimetypes.put("cmc","application/vnd.cosmocaller");
        mimetypes.put("cmdf","chemical/x-cmdf");
        mimetypes.put("cml","chemical/x-cml");
        mimetypes.put("cmp","application/vnd.yellowriver-custom-menu");
        mimetypes.put("cmx","image/x-cmx");
        mimetypes.put("cod","application/vnd.rim.cod");
        mimetypes.put("cpio","application/x-cpio");
        mimetypes.put("cpt","application/mac-compactpro");
        mimetypes.put("crd","application/x-mscardfile");
        mimetypes.put("crl","application/pkix-crl");
        mimetypes.put("cryptonote","application/vnd.rig.cryptonote");
        mimetypes.put("csh","application/x-csh");
        mimetypes.put("csml","chemical/x-csml");
        mimetypes.put("csp","application/vnd.commonspace");
        mimetypes.put("css","text/css");
        mimetypes.put("csv","text/csv");
        mimetypes.put("cu","application/cu-seeme");
        mimetypes.put("curl","text/vnd.curl");
        mimetypes.put("cww","application/prs.cww");
        mimetypes.put("dae","model/vnd.collada+xml");
        mimetypes.put("daf","application/vnd.mobius.daf");
        mimetypes.put("davmount","application/davmount+xml");
        mimetypes.put("dcurl","text/vnd.curl.dcurl");
        mimetypes.put("dd2","application/vnd.oma.dd2+xml");
        mimetypes.put("ddd","application/vnd.fujixerox.ddd");
        mimetypes.put("deb","application/x-debian-package");
        mimetypes.put("der","application/x-x509-ca-cert");
        mimetypes.put("dfac","application/vnd.dreamfactory");
        mimetypes.put("dir","application/x-director");
        mimetypes.put("dis","application/vnd.mobius.dis");
        mimetypes.put("djvu","image/vnd.djvu");
        mimetypes.put("dna","application/vnd.dna");
        mimetypes.put("doc","application/msword");
        mimetypes.put("docm","application/vnd.ms-word.document.macroenabled.12");
        mimetypes.put("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimetypes.put("dotm","application/vnd.ms-word.template.macroenabled.12");
        mimetypes.put("dotx","application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        mimetypes.put("dp","application/vnd.osgi.dp");
        mimetypes.put("dpg","application/vnd.dpgraph");
        mimetypes.put("dra","audio/vnd.dra");
        mimetypes.put("dsc","text/prs.lines.tag");
        mimetypes.put("dssc","application/dssc+der");
        mimetypes.put("dtb","application/x-dtbook+xml");
        mimetypes.put("dtd","application/xml-dtd");
        mimetypes.put("dts","audio/vnd.dts");
        mimetypes.put("dtshd","audio/vnd.dts.hd");
        mimetypes.put("dvi","application/x-dvi");
        mimetypes.put("dwf","model/vnd.dwf");
        mimetypes.put("dwg","image/vnd.dwg");
        mimetypes.put("dxf","image/vnd.dxf");
        mimetypes.put("dxp","application/vnd.spotfire.dxp");
        mimetypes.put("ecelp4800","audio/vnd.nuera.ecelp4800");
        mimetypes.put("ecelp7470","audio/vnd.nuera.ecelp7470");
        mimetypes.put("ecelp9600","audio/vnd.nuera.ecelp9600");
        mimetypes.put("edm","application/vnd.novadigm.edm");
        mimetypes.put("edx","application/vnd.novadigm.edx");
        mimetypes.put("efif","application/vnd.picsel");
        mimetypes.put("ei6","application/vnd.pg.osasli");
        mimetypes.put("eml","message/rfc822");
        mimetypes.put("emma","application/emma+xml");
        mimetypes.put("eol","audio/vnd.digital-winds");
        mimetypes.put("eot","application/vnd.ms-fontobject");
        mimetypes.put("epub","application/epub+zip");
        mimetypes.put("es","application/ecmascript");
        mimetypes.put("es3","application/vnd.eszigno3+xml");
        mimetypes.put("esf","application/vnd.epson.esf");
        mimetypes.put("etx","text/x-setext");
        mimetypes.put("exe","application/x-msdownload");
        mimetypes.put("exi","application/exi");
        mimetypes.put("ext","application/vnd.novadigm.ext");
        mimetypes.put("ez2","application/vnd.ezpix-album");
        mimetypes.put("ez3","application/vnd.ezpix-package");
        mimetypes.put("f", "text/x-fortran");
        mimetypes.put("f4v","video/x-f4v");
        mimetypes.put("fbs","image/vnd.fastbidsheet");
        mimetypes.put("fcs","application/vnd.isac.fcs");
        mimetypes.put("fdf","application/vnd.fdf");
        mimetypes.put("fe_launch","application/vnd.denovo.fcselayout-link");
        mimetypes.put("fg5","application/vnd.fujitsu.oasysgp");
        mimetypes.put("fh","image/x-freehand");
        mimetypes.put("fig","application/x-xfig");
        mimetypes.put("fli","video/x-fli");
        mimetypes.put("flo","application/vnd.micrografx.flo");
        mimetypes.put("flv","video/x-flv");
        mimetypes.put("flw","application/vnd.kde.kivio");
        mimetypes.put("flx","text/vnd.fmi.flexstor");
        mimetypes.put("fly","text/vnd.fly");
        mimetypes.put("fm","application/vnd.framemaker");
        mimetypes.put("fnc","application/vnd.frogans.fnc");
        mimetypes.put("fpx","image/vnd.fpx");
        mimetypes.put("fsc","application/vnd.fsc.weblaunch");
        mimetypes.put("fst","image/vnd.fst");
        mimetypes.put("ftc","application/vnd.fluxtime.clip");
        mimetypes.put("fti","application/vnd.anser-web-funds-transfer-initiation");
        mimetypes.put("fvt","video/vnd.fvt");
        mimetypes.put("fxp","application/vnd.adobe.fxp");
        mimetypes.put("fzs","application/vnd.fuzzysheet");
        mimetypes.put("g2w","application/vnd.geoplan");
        mimetypes.put("g3","image/g3fax");
        mimetypes.put("g3w","application/vnd.geospace");
        mimetypes.put("gac","application/vnd.groove-account");
        mimetypes.put("gdl","model/vnd.gdl");
        mimetypes.put("geo","application/vnd.dynageo");
        mimetypes.put("gex","application/vnd.geometry-explorer");
        mimetypes.put("ggb","application/vnd.geogebra.file");
        mimetypes.put("ggt","application/vnd.geogebra.tool");
        mimetypes.put("ghf","application/vnd.groove-help");
        mimetypes.put("gif","image/gif");
        mimetypes.put("gim","application/vnd.groove-identity-message");
        mimetypes.put("gmx","application/vnd.gmx");
        mimetypes.put("gnumeric","application/x-gnumeric");
        mimetypes.put("gph","application/vnd.flographit");
        mimetypes.put("gqf","application/vnd.grafeq");
        mimetypes.put("gram","application/srgs");
        mimetypes.put("grv","application/vnd.groove-injector");
        mimetypes.put("grxml","application/srgs+xml");
        mimetypes.put("gsf","application/x-font-ghostscript");
        mimetypes.put("gtar","application/x-gtar");
        mimetypes.put("gtm","application/vnd.groove-tool-message");
        mimetypes.put("gtw","model/vnd.gtw");
        mimetypes.put("gv","text/vnd.graphviz");
        mimetypes.put("gxt","application/vnd.geonext");
        mimetypes.put("h261","video/h261");
        mimetypes.put("h263","video/h263");
        mimetypes.put("h264","video/h264");
        mimetypes.put("hal","application/vnd.hal+xml");
        mimetypes.put("hbci","application/vnd.hbci");
        mimetypes.put("hdf","application/x-hdf");
        mimetypes.put("hlp","application/winhlp");
        mimetypes.put("hpgl","application/vnd.hp-hpgl");
        mimetypes.put("hpid","application/vnd.hp-hpid");
        mimetypes.put("hps","application/vnd.hp-hps");
        mimetypes.put("hqx","application/mac-binhex40");
        mimetypes.put("htke","application/vnd.kenameaapp");
        mimetypes.put("html","text/html");
        mimetypes.put("hvd","application/vnd.yamaha.hv-dic");
        mimetypes.put("hvp","application/vnd.yamaha.hv-voice");
        mimetypes.put("hvs","application/vnd.yamaha.hv-script");
        mimetypes.put("i2g","application/vnd.intergeo");
        mimetypes.put("icc","application/vnd.iccprofile");
        mimetypes.put("ice","x-conference/x-cooltalk");
        mimetypes.put("ico","image/x-icon");
        mimetypes.put("ics","text/calendar");
        mimetypes.put("ief","image/ief");
        mimetypes.put("ifm","application/vnd.shana.informed.formdata");
        mimetypes.put("igl","application/vnd.igloader");
        mimetypes.put("igm","application/vnd.insors.igm");
        mimetypes.put("igs","model/iges");
        mimetypes.put("igx","application/vnd.micrografx.igx");
        mimetypes.put("iif","application/vnd.shana.informed.interchange");
        mimetypes.put("imp","application/vnd.accpac.simply.imp");
        mimetypes.put("ims","application/vnd.ms-ims");
        mimetypes.put("ipfix","application/ipfix");
        mimetypes.put("ipk","application/vnd.shana.informed.package");
        mimetypes.put("irm","application/vnd.ibm.rights-management");
        mimetypes.put("irp","application/vnd.irepository.package+xml");
        mimetypes.put("itp","application/vnd.shana.informed.formtemplate");
        mimetypes.put("ivp","application/vnd.immervision-ivp");
        mimetypes.put("ivu","application/vnd.immervision-ivu");
        mimetypes.put("jad","text/vnd.sun.j2me.app-descriptor");
        mimetypes.put("jam","application/vnd.jam");
        mimetypes.put("jar","application/java-archive");
        mimetypes.put("java","text/x-java-source,java");
        mimetypes.put("jisp","application/vnd.jisp");
        mimetypes.put("jlt","application/vnd.hp-jlyt");
        mimetypes.put("jnlp","application/x-java-jnlp-file");
        mimetypes.put("joda","application/vnd.joost.joda-archive");
        mimetypes.put("jpeg","image/jpeg");
        mimetypes.put("jpg","image/jpeg");
        mimetypes.put("jpgv","video/jpeg");
        mimetypes.put("jpm","video/jpm");
        mimetypes.put("js","application/javascript");
        mimetypes.put("json","application/json");
        mimetypes.put("karbon", "application/vnd.kde.karbon");
        mimetypes.put("kfo","application/vnd.kde.kformula");
        mimetypes.put("kia","application/vnd.kidspiration");
        mimetypes.put("kml","application/vnd.google-earth.kml+xml");
        mimetypes.put("kmz","application/vnd.google-earth.kmz");
        mimetypes.put("kne","application/vnd.kinar");
        mimetypes.put("kon","application/vnd.kde.kontour");
        mimetypes.put("kpr","application/vnd.kde.kpresenter");
        mimetypes.put("ksp","application/vnd.kde.kspread");
        mimetypes.put("ktx","image/ktx");
        mimetypes.put("ktz","application/vnd.kahootz");
        mimetypes.put("kwd","application/vnd.kde.kword");
        mimetypes.put("lasxml","application/vnd.las.las+xml");
        mimetypes.put("latex","application/x-latex");
        mimetypes.put("lbd","application/vnd.llamagraphics.life-balance.desktop");
        mimetypes.put("lbe","application/vnd.llamagraphics.life-balance.exchange+xml");
        mimetypes.put("les","application/vnd.hhe.lesson-player");
        mimetypes.put("link66","application/vnd.route66.link66+xml");
        mimetypes.put("lrm","application/vnd.ms-lrm");
        mimetypes.put("ltf","application/vnd.frogans.ltf");
        mimetypes.put("lvp","audio/vnd.lucent.voice");
        mimetypes.put("lwp","application/vnd.lotus-wordpro");
        mimetypes.put("m21","application/mp21");
        mimetypes.put("m3u","audio/x-mpegurl");
        mimetypes.put("m3u8","application/vnd.apple.mpegurl");
        mimetypes.put("m4v","video/x-m4v");
        mimetypes.put("ma","application/mathematica");
        mimetypes.put("mads","application/mads+xml");
        mimetypes.put("mag","application/vnd.ecowin.chart");
        mimetypes.put("map","application/json");
        mimetypes.put("mathml","application/mathml+xml");
        mimetypes.put("mbk","application/vnd.mobius.mbk");
        mimetypes.put("mbox","application/mbox");
        mimetypes.put("mc1","application/vnd.medcalcdata");
        mimetypes.put("mcd","application/vnd.mcd");
        mimetypes.put("mcurl","text/vnd.curl.mcurl");
        mimetypes.put("md","text/x-markdown");
        mimetypes.put("mdb","application/x-msaccess");
        mimetypes.put("mdi","image/vnd.ms-modi");
        mimetypes.put("meta4","application/metalink4+xml");
        mimetypes.put("mets","application/mets+xml");
        mimetypes.put("mfm","application/vnd.mfmp");
        mimetypes.put("mgp","application/vnd.osgeo.mapguide.package");
        mimetypes.put("mgz","application/vnd.proteus.magazine");
        mimetypes.put("mid","audio/midi");
        mimetypes.put("mif","application/vnd.mif");
        mimetypes.put("mj2","video/mj2");
        mimetypes.put("mlp","application/vnd.dolby.mlp");
        mimetypes.put("mmd","application/vnd.chipnuts.karaoke-mmd");
        mimetypes.put("mmf","application/vnd.smaf");
        mimetypes.put("mmr","image/vnd.fujixerox.edmics-mmr");
        mimetypes.put("mny","application/x-msmoney");
        mimetypes.put("mods","application/mods+xml");
        mimetypes.put("movie","video/x-sgi-movie");
        mimetypes.put("mp1","audio/mpeg");
        mimetypes.put("mp2","audio/mpeg");
        mimetypes.put("mp3","audio/mpeg");
        mimetypes.put("mp4","video/mp4");
        mimetypes.put("mp4a","audio/mp4");
        mimetypes.put("mpc","application/vnd.mophun.certificate");
        mimetypes.put("mpeg","video/mpeg");
        mimetypes.put("mpga","audio/mpeg");
        mimetypes.put("mpkg","application/vnd.apple.installer+xml");
        mimetypes.put("mpm","application/vnd.blueice.multipass");
        mimetypes.put("mpn","application/vnd.mophun.application");
        mimetypes.put("mpp","application/vnd.ms-project");
        mimetypes.put("mpy","application/vnd.ibm.minipay");
        mimetypes.put("mqy","application/vnd.mobius.mqy");
        mimetypes.put("mrc","application/marc");
        mimetypes.put("mrcx","application/marcxml+xml");
        mimetypes.put("mscml","application/mediaservercontrol+xml");
        mimetypes.put("mseq","application/vnd.mseq");
        mimetypes.put("msf","application/vnd.epson.msf");
        mimetypes.put("msh","model/mesh");
        mimetypes.put("msl","application/vnd.mobius.msl");
        mimetypes.put("msty","application/vnd.muvee.style");
        mimetypes.put("mts","model/vnd.mts");
        mimetypes.put("mus","application/vnd.musician");
        mimetypes.put("musicxml","application/vnd.recordare.musicxml+xml");
        mimetypes.put("mvb","application/x-msmediaview");
        mimetypes.put("mwf","application/vnd.mfer");
        mimetypes.put("mxf","application/mxf");
        mimetypes.put("mxl","application/vnd.recordare.musicxml");
        mimetypes.put("mxml","application/xv+xml");
        mimetypes.put("mxs","application/vnd.triscape.mxs");
        mimetypes.put("mxu","video/vnd.mpegurl");
        mimetypes.put("n3","text/n3");
        mimetypes.put("nbp","application/vnd.wolfram.player");
        mimetypes.put("nc","application/x-netcdf");
        mimetypes.put("ncx","application/x-dtbncx+xml");
        mimetypes.put("n-gage","application/vnd.nokia.n-gage.symbian.install");
        mimetypes.put("ngdat","application/vnd.nokia.n-gage.data");
        mimetypes.put("nlu","application/vnd.neurolanguage.nlu");
        mimetypes.put("nml","application/vnd.enliven");
        mimetypes.put("nnd","application/vnd.noblenet-directory");
        mimetypes.put("nns","application/vnd.noblenet-sealer");
        mimetypes.put("nnw","application/vnd.noblenet-web");
        mimetypes.put("npx","image/vnd.net-fpx");
        mimetypes.put("nsf","application/vnd.lotus-notes");
        mimetypes.put("oa2","application/vnd.fujitsu.oasys2");
        mimetypes.put("oa3","application/vnd.fujitsu.oasys3");
        mimetypes.put("oas","application/vnd.fujitsu.oasys");
        mimetypes.put("obd","application/x-msbinder");
        mimetypes.put("oda","application/oda");
        mimetypes.put("odb","application/vnd.oasis.opendocument.database");
        mimetypes.put("odc","application/vnd.oasis.opendocument.chart");
        mimetypes.put("odf","application/vnd.oasis.opendocument.formula");
        mimetypes.put("odft","application/vnd.oasis.opendocument.formula-template");
        mimetypes.put("odg","application/vnd.oasis.opendocument.graphics");
        mimetypes.put("odi","application/vnd.oasis.opendocument.image");
        mimetypes.put("odm","application/vnd.oasis.opendocument.text-master");
        mimetypes.put("odp","application/vnd.oasis.opendocument.presentation");
        mimetypes.put("ods","application/vnd.oasis.opendocument.spreadsheet");
        mimetypes.put("odt","application/vnd.oasis.opendocument.text");
        mimetypes.put("oga","audio/ogg");
        mimetypes.put("ogv","video/ogg");
        mimetypes.put("ogx","application/ogg");
        mimetypes.put("onetoc","application/onenote");
        mimetypes.put("opf","application/oebps-package+xml");
        mimetypes.put("org","application/vnd.lotus-organizer");
        mimetypes.put("osf","application/vnd.yamaha.openscoreformat");
        mimetypes.put("osfpvg","application/vnd.yamaha.openscoreformat.osfpvg+xml");
        mimetypes.put("otc","application/vnd.oasis.opendocument.chart-template");
        mimetypes.put("otf","application/x-font-otf");
        mimetypes.put("otg","application/vnd.oasis.opendocument.graphics-template");
        mimetypes.put("oth","application/vnd.oasis.opendocument.text-web");
        mimetypes.put("oti","application/vnd.oasis.opendocument.image-template");
        mimetypes.put("otp","application/vnd.oasis.opendocument.presentation-template");
        mimetypes.put("ots","application/vnd.oasis.opendocument.spreadsheet-template");
        mimetypes.put("ott","application/vnd.oasis.opendocument.text-template");
        mimetypes.put("oxt","application/vnd.openofficeorg.extension");
        mimetypes.put("p","text/x-pascal");
        mimetypes.put("p10","application/pkcs10");
        mimetypes.put("p12","application/x-pkcs12");
        mimetypes.put("p7b","application/x-pkcs7-certificates");
        mimetypes.put("p7m","application/pkcs7-mime");
        mimetypes.put("p7r","application/x-pkcs7-certreqresp");
        mimetypes.put("p7s","application/pkcs7-signature");
        mimetypes.put("p8","application/pkcs8");
        mimetypes.put("par","text/plain-bas");
        mimetypes.put("paw","application/vnd.pawaafile");
        mimetypes.put("pbd","application/vnd.powerbuilder6");
        mimetypes.put("pbm","image/x-portable-bitmap");
        mimetypes.put("pcf","application/x-font-pcf");
        mimetypes.put("pcl","application/vnd.hp-pcl");
        mimetypes.put("pclxl","application/vnd.hp-pclxl");
        mimetypes.put("pcurl","application/vnd.curl.pcurl");
        mimetypes.put("pcx","image/x-pcx");
        mimetypes.put("pdb","application/vnd.palm");
        mimetypes.put("pdf","application/pdf");
        mimetypes.put("pfa","application/x-font-type1");
        mimetypes.put("pfr","application/font-tdpfr");
        mimetypes.put("pgm","image/x-portable-graymap");
        mimetypes.put("pgn","application/x-chess-pgn");
        mimetypes.put("pgp","application/pgp-signature");
        mimetypes.put("pic","image/x-pict");
        mimetypes.put("pki","application/pkixcmp");
        mimetypes.put("pkipath","application/pkix-pkipath");
        mimetypes.put("plb","application/vnd.3gpp.pic-bw-large");
        mimetypes.put("plc","application/vnd.mobius.plc");
        mimetypes.put("plf","application/vnd.pocketlearn");
        mimetypes.put("pls","application/pls+xml");
        mimetypes.put("pml","application/vnd.ctc-posml");
        mimetypes.put("png","image/png");
        mimetypes.put("pnm","image/x-portable-anymap");
        mimetypes.put("portpkg","application/vnd.macports.portpkg");
        mimetypes.put("potm","application/vnd.ms-powerpoint.template.macroenabled.12");
        mimetypes.put("potx","application/vnd.openxmlformats-officedocument.presentationml.template");
        mimetypes.put("ppam","application/vnd.ms-powerpoint.addin.macroenabled.12");
        mimetypes.put("ppd","application/vnd.cups-ppd");
        mimetypes.put("ppm","image/x-portable-pixmap");
        mimetypes.put("ppsm","application/vnd.ms-powerpoint.slideshow.macroenabled.12");
        mimetypes.put("ppsx","application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        mimetypes.put("ppt","application/vnd.ms-powerpoint");
        mimetypes.put("pptm","application/vnd.ms-powerpoint.presentation.macroenabled.12");
        mimetypes.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mimetypes.put("prc","application/x-mobipocket-ebook");
        mimetypes.put("pre","application/vnd.lotus-freelance");
        mimetypes.put("prf","application/pics-rules");
        mimetypes.put("psb","application/vnd.3gpp.pic-bw-small");
        mimetypes.put("psd","image/vnd.adobe.photoshop");
        mimetypes.put("psf","application/x-font-linux-psf");
        mimetypes.put("pskcxml","application/pskc+xml");
        mimetypes.put("ptid","application/vnd.pvi.ptid1");
        mimetypes.put("pub","application/x-mspublisher");
        mimetypes.put("pvb","application/vnd.3gpp.pic-bw-var");
        mimetypes.put("pwn","application/vnd.3m.post-it-notes");
        mimetypes.put("pya","audio/vnd.ms-playready.media.pya");
        mimetypes.put("pyv","video/vnd.ms-playready.media.pyv");
        mimetypes.put("qam","application/vnd.epson.quickanime");
        mimetypes.put("qbo","application/vnd.intu.qbo");
        mimetypes.put("qfx","application/vnd.intu.qfx");
        mimetypes.put("qps","application/vnd.publishare-delta-tree");
        mimetypes.put("qt","video/quicktime");
        mimetypes.put("qxd","application/vnd.quark.quarkxpress");
        mimetypes.put("ram","audio/x-pn-realaudio");
        mimetypes.put("rar","application/x-rar-compressed");
        mimetypes.put("ras","image/x-cmu-raster");
        mimetypes.put("rcprofile","application/vnd.ipunplugged.rcprofile");
        mimetypes.put("rdf","application/rdf+xml");
        mimetypes.put("rdz","application/vnd.data-vision.rdz");
        mimetypes.put("rep","application/vnd.businessobjects");
        mimetypes.put("res","application/x-dtbresource+xml");
        mimetypes.put("rgb","image/x-rgb");
        mimetypes.put("rif","application/reginfo+xml");
        mimetypes.put("rip","audio/vnd.rip");
        mimetypes.put("rl","application/resource-lists+xml");
        mimetypes.put("rlc","image/vnd.fujixerox.edmics-rlc");
        mimetypes.put("rld","application/resource-lists-diff+xml");
        mimetypes.put("rm","application/vnd.rn-realmedia");
        mimetypes.put("rmp","audio/x-pn-realaudio-plugin");
        mimetypes.put("rms","application/vnd.jcp.javame.midlet-rms");
        mimetypes.put("rnc","application/relax-ng-compact-syntax");
        mimetypes.put("rp9","application/vnd.cloanto.rp9");
        mimetypes.put("rpss","application/vnd.nokia.radio-presets");
        mimetypes.put("rpst","application/vnd.nokia.radio-preset");
        mimetypes.put("rq","application/sparql-query");
        mimetypes.put("rs","application/rls-services+xml");
        mimetypes.put("rsd","application/rsd+xml");
        mimetypes.put("rss","application/rss+xml");
        mimetypes.put("rtf","application/rtf");
        mimetypes.put("rtx","text/richtext");
        mimetypes.put("s","text/x-asm");
        mimetypes.put("saf","application/vnd.yamaha.smaf-audio");
        mimetypes.put("sbml","application/sbml+xml");
        mimetypes.put("sc","application/vnd.ibm.secure-container");
        mimetypes.put("scd","application/x-msschedule");
        mimetypes.put("scm","application/vnd.lotus-screencam");
        mimetypes.put("scq","application/scvp-cv-request");
        mimetypes.put("scs","application/scvp-cv-response");
        mimetypes.put("scurl","text/vnd.curl.scurl");
        mimetypes.put("sda","application/vnd.stardivision.draw");
        mimetypes.put("sdc","application/vnd.stardivision.calc");
        mimetypes.put("sdd","application/vnd.stardivision.impress");
        mimetypes.put("sdkm","application/vnd.solent.sdkm+xml");
        mimetypes.put("sdp","application/sdp");
        mimetypes.put("sdw","application/vnd.stardivision.writer");
        mimetypes.put("see","application/vnd.seemail");
        mimetypes.put("seed","application/vnd.fdsn.seed");
        mimetypes.put("sema","application/vnd.sema");
        mimetypes.put("semd","application/vnd.semd");
        mimetypes.put("semf","application/vnd.semf");
        mimetypes.put("ser","application/java-serialized-object");
        mimetypes.put("setpay","application/set-payment-initiation");
        mimetypes.put("setreg","application/set-registration-initiation");
        mimetypes.put("sfd-hdstx","application/vnd.hydrostatix.sof-data");
        mimetypes.put("sfs","application/vnd.spotfire.sfs");
        mimetypes.put("sgl","application/vnd.stardivision.writer-global");
        mimetypes.put("sgml","text/sgml");
        mimetypes.put("sh","application/x-sh");
        mimetypes.put("shar","application/x-shar");
        mimetypes.put("shf","application/shf+xml");
        mimetypes.put("sis","application/vnd.symbian.install");
        mimetypes.put("sit","application/x-stuffit");
        mimetypes.put("sitx","application/x-stuffitx");
        mimetypes.put("skp","application/vnd.koan");
        mimetypes.put("sldm","application/vnd.ms-powerpoint.slide.macroenabled.12");
        mimetypes.put("sldx","application/vnd.openxmlformats-officedocument.presentationml.slide");
        mimetypes.put("slt","application/vnd.epson.salt");
        mimetypes.put("sm","application/vnd.stepmania.stepchart");
        mimetypes.put("smf","application/vnd.stardivision.math");
        mimetypes.put("smi","application/smil+xml");
        mimetypes.put("snf","application/x-font-snf");
        mimetypes.put("spf","application/vnd.yamaha.smaf-phrase");
        mimetypes.put("spl","application/x-futuresplash");
        mimetypes.put("spot","text/vnd.in3d.spot");
        mimetypes.put("spp","application/scvp-vp-response");
        mimetypes.put("spq","application/scvp-vp-request");
        mimetypes.put("src","application/x-wais-source");
        mimetypes.put("sru","application/sru+xml");
        mimetypes.put("srx","application/sparql-results+xml");
        mimetypes.put("sse","application/vnd.kodak-descriptor");
        mimetypes.put("ssf","application/vnd.epson.ssf");
        mimetypes.put("ssml","application/ssml+xml");
        mimetypes.put("st","application/vnd.sailingtracker.track");
        mimetypes.put("stc","application/vnd.sun.xml.calc.template");
        mimetypes.put("std","application/vnd.sun.xml.draw.template");
        mimetypes.put("stf","application/vnd.wt.stf");
        mimetypes.put("sti","application/vnd.sun.xml.impress.template");
        mimetypes.put("stk","application/hyperstudio");
        mimetypes.put("stl","application/vnd.ms-pki.stl");
        mimetypes.put("str","application/vnd.pg.format");
        mimetypes.put("stw","application/vnd.sun.xml.writer.template");
        mimetypes.put("sub","image/vnd.dvb.subtitle");
        mimetypes.put("sus","application/vnd.sus-calendar");
        mimetypes.put("sv4cpio","application/x-sv4cpio");
        mimetypes.put("sv4crc","application/x-sv4crc");
        mimetypes.put("svc","application/vnd.dvb.service");
        mimetypes.put("svd","application/vnd.svd");
        mimetypes.put("svg","image/svg+xml");
        mimetypes.put("swf","application/x-shockwave-flash");
        mimetypes.put("swi","application/vnd.aristanetworks.swi");
        mimetypes.put("sxc","application/vnd.sun.xml.calc");
        mimetypes.put("sxd","application/vnd.sun.xml.draw");
        mimetypes.put("sxg","application/vnd.sun.xml.writer.global");
        mimetypes.put("sxi","application/vnd.sun.xml.impress");
        mimetypes.put("sxm","application/vnd.sun.xml.math");
        mimetypes.put("sxw","application/vnd.sun.xml.writer");
        mimetypes.put("t","text/troff");
        mimetypes.put("tao","application/vnd.tao.intent-module-archive");
        mimetypes.put("tar","application/x-tar");
        mimetypes.put("tcap","application/vnd.3gpp2.tcap");
        mimetypes.put("tcl","application/x-tcl");
        mimetypes.put("teacher","application/vnd.smart.teacher");
        mimetypes.put("tei","application/tei+xml");
        mimetypes.put("tex","application/x-tex");
        mimetypes.put("texinfo","application/x-texinfo");
        mimetypes.put("tfi","application/thraud+xml");
        mimetypes.put("tfm","application/x-tex-tfm");
        mimetypes.put("thmx","application/vnd.ms-officetheme");
        mimetypes.put("tiff","image/tiff");
        mimetypes.put("tmo","application/vnd.tmobile-livetv");
        mimetypes.put("torrent","application/x-bittorrent");
        mimetypes.put("tpl","application/vnd.groove-tool-template");
        mimetypes.put("tpt","application/vnd.trid.tpt");
        mimetypes.put("tra","application/vnd.trueapp");
        mimetypes.put("trm","application/x-msterminal");
        mimetypes.put("tsd","application/timestamped-data");
        mimetypes.put("tsv","text/tab-separated-values");
        mimetypes.put("ttf","application/x-font-ttf");
        mimetypes.put("ttl","text/turtle");
        mimetypes.put("twd","application/vnd.simtech-mindmapper");
        mimetypes.put("txd","application/vnd.genomatix.tuxedo");
        mimetypes.put("txf","application/vnd.mobius.txf");
        mimetypes.put("txt","text/plain");
        mimetypes.put("ufd","application/vnd.ufdl");
        mimetypes.put("umj","application/vnd.umajin");
        mimetypes.put("unityweb", "application/vnd.unity");
        mimetypes.put("uoml","application/vnd.uoml+xml");
        mimetypes.put("uri","text/uri-list");
        mimetypes.put("ustar","application/x-ustar");
        mimetypes.put("utz","application/vnd.uiq.theme");
        mimetypes.put("uu","text/x-uuencode");
        mimetypes.put("uva","audio/vnd.dece.audio");
        mimetypes.put("uvh","video/vnd.dece.hd");
        mimetypes.put("uvi","image/vnd.dece.graphic");
        mimetypes.put("uvm","video/vnd.dece.mobile");
        mimetypes.put("uvp","video/vnd.dece.pd");
        mimetypes.put("uvs","video/vnd.dece.sd");
        mimetypes.put("uvu","video/vnd.uvvu.mp4");
        mimetypes.put("uvv","video/vnd.dece.video");
        mimetypes.put("vcd","application/x-cdlink");
        mimetypes.put("vcf","text/x-vcard");
        mimetypes.put("vcg","application/vnd.groove-vcard");
        mimetypes.put("vcs","text/x-vcalendar");
        mimetypes.put("vcx","application/vnd.vcx");
        mimetypes.put("vis","application/vnd.visionary");
        mimetypes.put("viv","video/vnd.vivo");
        mimetypes.put("vsd","application/vnd.visio");
        mimetypes.put("vsf","application/vnd.vsf");
        mimetypes.put("vtu","model/vnd.vtu");
        mimetypes.put("vxml","application/voicexml+xml");
        mimetypes.put("wad","application/x-doom");
        mimetypes.put("wav","audio/x-wav");
        mimetypes.put("wax","audio/x-ms-wax");
        mimetypes.put("wbmp","image/vnd.wap.wbmp");
        mimetypes.put("wbs","application/vnd.criticaltools.wbs+xml");
        mimetypes.put("wbxml","application/vnd.wap.wbxml");
        mimetypes.put("weba","audio/webm");
        mimetypes.put("webm","video/webm");
        mimetypes.put("webp","image/webp");
        mimetypes.put("wg","application/vnd.pmi.widget");
        mimetypes.put("wgt","application/widget");
        mimetypes.put("wm","video/x-ms-wm");
        mimetypes.put("wma","audio/x-ms-wma");
        mimetypes.put("wmd","application/x-ms-wmd");
        mimetypes.put("wmf","application/x-msmetafile");
        mimetypes.put("wml","text/vnd.wap.wml");
        mimetypes.put("wmlc","application/vnd.wap.wmlc");
        mimetypes.put("wmls","text/vnd.wap.wmlscript");
        mimetypes.put("wmlsc","application/vnd.wap.wmlscriptc");
        mimetypes.put("wmv","video/x-ms-wmv");
        mimetypes.put("wmx","video/x-ms-wmx");
        mimetypes.put("wmz","application/x-ms-wmz");
        mimetypes.put("woff","application/x-font-woff");
        mimetypes.put("woff2","application/font-woff2");
        mimetypes.put("wpd","application/vnd.wordperfect");
        mimetypes.put("wpl","application/vnd.ms-wpl");
        mimetypes.put("wps","application/vnd.ms-works");
        mimetypes.put("wqd","application/vnd.wqd");
        mimetypes.put("wri","application/x-mswrite");
        mimetypes.put("wrl","model/vrml");
        mimetypes.put("wsdl","application/wsdl+xml");
        mimetypes.put("wspolicy","application/wspolicy+xml");
        mimetypes.put("wtb","application/vnd.webturbo");
        mimetypes.put("wvx","video/x-ms-wvx");
        mimetypes.put("x3d","application/vnd.hzn-3d-crossword");
        mimetypes.put("xap","application/x-silverlight-app");
        mimetypes.put("xar","application/vnd.xara");
        mimetypes.put("xbap","application/x-ms-xbap");
        mimetypes.put("xbd","application/vnd.fujixerox.docuworks.binder");
        mimetypes.put("xbm","image/x-xbitmap");
        mimetypes.put("xdf","application/xcap-diff+xml");
        mimetypes.put("xdm","application/vnd.syncml.dm+xml");
        mimetypes.put("xdp","application/vnd.adobe.xdp+xml");
        mimetypes.put("xdssc","application/dssc+xml");
        mimetypes.put("xdw","application/vnd.fujixerox.docuworks");
        mimetypes.put("xenc","application/xenc+xml");
        mimetypes.put("xer","application/patch-ops-error+xml");
        mimetypes.put("xfdf","application/vnd.adobe.xfdf");
        mimetypes.put("xfdl","application/vnd.xfdl");
        mimetypes.put("xhtml","application/xhtml+xml");
        mimetypes.put("xif","image/vnd.xiff");
        mimetypes.put("xlam","application/vnd.ms-excel.addin.macroenabled.12");
        mimetypes.put("xls","application/vnd.ms-excel");
        mimetypes.put("xlsb","application/vnd.ms-excel.sheet.binary.macroenabled.12");
        mimetypes.put("xlsm","application/vnd.ms-excel.sheet.macroenabled.12");
        mimetypes.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimetypes.put("xltm","application/vnd.ms-excel.template.macroenabled.12");
        mimetypes.put("xltx","application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        mimetypes.put("xml","application/xml");
        mimetypes.put("xo","application/vnd.olpc-sugar");
        mimetypes.put("xop","application/xop+xml");
        mimetypes.put("xpi","application/x-xpinstall");
        mimetypes.put("xpm","image/x-xpixmap");
        mimetypes.put("xpr","application/vnd.is-xpr");
        mimetypes.put("xps","application/vnd.ms-xpsdocument");
        mimetypes.put("xpw","application/vnd.intercon.formnet");
        mimetypes.put("xslt","application/xslt+xml");
        mimetypes.put("xsm","application/vnd.syncml+xml");
        mimetypes.put("xspf","application/xspf+xml");
        mimetypes.put("xul","application/vnd.mozilla.xul+xml");
        mimetypes.put("xwd","image/x-xwindowdump");
        mimetypes.put("xyz","chemical/x-xyz");
        mimetypes.put("yaml","text/yaml");
        mimetypes.put("yang","application/yang");
        mimetypes.put("yin","application/yin+xml");
        mimetypes.put("zaz","application/vnd.zzazz.deck+xml");
        mimetypes.put("zip","application/zip");
        mimetypes.put("zir","application/vnd.zul");
        mimetypes.put("zmm","application/vnd.handheld-entertainment+xml");
    }

    public static String sanitize(String var) {
        String sanitized = var.replaceAll("\\<.*?>","");
        sanitized = sanitized.replaceAll("http://", "");
        sanitized = sanitized.replaceAll("https://", "");
        sanitized = sanitized.replaceAll("\\.\\./", "");
        return sanitized;
    }

    public static boolean contains(String where, String what){
        boolean retval = false;

        String[] tmp = where.split(",");
        for (int i = 0; i < tmp.length; i++) {
            if (what.equalsIgnoreCase(tmp[i])){
                retval = true;
                break;
            }
        }
        return retval;
    }


    public static Dimension getImageSize(String path){

        BufferedImage readImage = null;
        Dimension dim = new Dimension();
        try {
            readImage = ImageIO.read(new File(path));
            dim.height = readImage.getHeight();
            dim.width = readImage.getWidth();
        } catch (Exception e) {
            readImage = null;
        }
        return dim;
    }

    public static Map getDefaultItem(){
        Map defaultInfo = new HashMap<>();
        defaultInfo.put("Path", "");
        defaultInfo.put("Filename", "");
        defaultInfo.put("Protected", "");
        defaultInfo.put("Thumbnail", "");
        defaultInfo.put("Preview", "");
        defaultInfo.put("Error", "");
        defaultInfo.put("Code", 0);
        return defaultInfo;
    }

    public static Map getDefaultProperties(){
        Map<String, Object> properties = new HashMap<>();
        properties.put("Date Created", "");
        properties.put("Date Modified", "");
        properties.put("filemtime", "");
        properties.put("Width", "");
        properties.put("Size", "");
        return properties;
    }

    public static void removeRecursive(Path path) throws IOException
    {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc == null)
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                else
                {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }

    public static JSONObject getDirSummary(Path path) throws IOException
    {

        final Map<String, Long> result =  new HashMap<>();
        result.put("numberFile", 0L);
        result.put("numberFolder", 0L);
        result.put("totalSize", 0L);

        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                result.put("numberFile", (result.get("numberFile")) + 1);
                result.put("totalSize", (result.get("totalSize")) + Files.size(file));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                result.put("numberFile", (result.get("numberFile")) + 1);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc == null)
                {
                    result.put("numberFolder", (result.get("numberFolder")) + 1);
                    return FileVisitResult.CONTINUE;
                }
                else
                {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });

        JSONObject object = new JSONObject();
        object.put("Size", result.get("totalSize") );
        object.put("Files", result.get("numberFile") );
        object.put("Folders", result.get("numberFolder") );
        object.put("Error", "");
        object.put("Code", 0);
        return object;
    }


}