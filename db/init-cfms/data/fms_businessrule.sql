/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 5.0.96-community-nt 
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('1','rule_qr','二维码规则','$CID:{cid},MID:{mid},FID:{fid},WID:{wid},WNAME:{wname},WVERSION:{wversion},TIME:{time},ENTRY:{entry}#',NULL,'1','','1','V1');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('2','rule_file','案卷档号规则','$PRJ:{prj},SECTION:{section},ENGIN:{engin},STATION:{station},TYPE:{type},FNUM:0000#',NULL,'2','','1','V1');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('3','rule_filedoc','文件档号规则','$PRJ:{prj},SECTION:{section},ENGIN:{engin},STATION:{station},TYPE:{type},FNUM:000,DNUM:000#',NULL,'3','','1','V1');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('4','rule_archive','案卷题名规则','$CITY:{city},PRJNAME:{prjname},MAJOR:{major},MENUTYPE:{menutype},MODELTYPE:{modeltype},MODEL:{model}#',NULL,'4','','1','V1');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('5','rule_smartsplit','智能拆分规则','$PAGEAVG:200,PAGEFLOAT:15%,PAGEMAX:230,DOCSORT:true,MANYTYPE:true,RESET:ture,SUF:true#',NULL,'5','','1','V1');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('6','rule_qr','二维码规则','$CID:{cid},MID:{mid},FID:{fid},WID:{wid},WNAME:{wname},WVERSION:{wversion},TIME:{time},ENTRY:{MD5}#',NULL,'1','','1','V2');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('7','rule_qr','二维码规则','$CID:{cid},MID:{mid},FID:{fid},WID:{wid},WNAME:{wname},WVERSION:{wversion},TIME:{time}#',NULL,'1','','1','V3');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('8','rule_qr','二维码规则','$CID:{cid},MID:{mid},FID:{fid},WID:{wid},WNAME:{wname},WVERSION:{wversion},TIME:{data-time},ENTRY:{entry}#',NULL,'1','','1','V4');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('9','rule_qr','二维码规则','$CID:{cid},MID:{mid},FID:{fid},WID:{wid},WNAME:{wname},WVERSION:{wversion},TIME:{time},ENTRY:{MD5+entry}#',NULL,'1','','1','V5');
insert into `fms_businessrule` (`ruleid`, `rulecode`, `rulename`, `ruleexpress`, `instanse`, `sort`, `remark`, `status`, `ruleversion`) values('10','rule_qr','二维码规则','$CID:{cid},MID:{mid},FID:{fid},WID:{wid},WNAME:{wname},WVERSION:{wversion},TIME:{time},ENTRY:{entry+MD5}#',NULL,'1','','1','V5.2');
