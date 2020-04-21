function start() {
    status = -1;
    action(1, 0, 0);
}

var Bgm = [
	
	["Bgm00.img/SleepyWood","SleepyWood","睡莲"],
	["Bgm00.img/FloralLife","FloralLife","集市"],
	["Bgm00.img/GoPicnic","GoPicnic","都市"],
	["Bgm00.img/Nightmare","Nightmare","部落"],
	["Bgm00.img/RestNPeace","RestNPeace","赶集"],
	
	["Bgm01.img/AncientMove","AncientMove","古移"],
	["Bgm01.img/MoonlightShadow","MoonlightShadow","月光阴影"],
	["Bgm01.img/WhereTheBarlogFrom","WhereTheBarlogFrom","酒吧"],
	["Bgm01.img/CavaBien","CavaBien","集市2"],
	["Bgm01.img/HighlandStar","HighlandStar","高地"],
	["Bgm01.img/BadGuys","BadGuys","都市2"],
	
	["Bgm02.img/MissingYou","MissingYou","想你"],
	["Bgm02.img/WhenTheMorningComes","WhenTheMorningComes","黎明来临"],
	["Bgm02.img/EvilEyes","EvilEyes","邪恶眼睛"],
	["Bgm02.img/JungleBook","JungleBook","丛林"],
	["Bgm02.img/AboveTheTreetops","AboveTheTreetops","树梢"],
	
	["Bgm03.img/Subway","Subway","地铁"],
	["Bgm03.img/Elfwood","Elfwood","精灵木"],
	["Bgm03.img/BlueSky","BlueSky","布鲁斯基"],
	["Bgm03.img/Beachway","Beachway","海滩"],
	["Bgm03.img/SnowyVillage","SnowyVillage","雪村"],
	
	["Bgm04.img/PlayWithMe","PlayWithMe","一起玩"],
	["Bgm04.img/WhiteChristmas","WhiteChristmas","白色圣诞节"],
	["Bgm04.img/UponTheSky","UponTheSky","天空"],
	["Bgm04.img/ArabPirate","ArabPirate","花生酸盐"],
	["Bgm04.img/WarmRegard","WarmRegard","温雷格"],
	
]



function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	
    if (status == 0) {
        var 文本 = "  #i4030000#  #i4030001#  #i4030010# #r#e< Bgm 点播 >#k#n #i4030011#  #i4030014#  #i4030015#\r\n\r\n";
		
		for (var ii = 0; ii < Bgm.length; ii++) {
			文本 += "\t\t     #L"+ii+"# #b"+Bgm[ii][2]+" \ "+Bgm[ii][1]+"#k#l\r\n";
		}
        cm.sendSimple(文本);
    } else if (status == 1) {
        switch (selection) {
			default:
				if(cm.getbosslog("点播BGM") < 10){
					cm.setbosslog("点播BGM");
					cm.changeMusic(Bgm[selection][0]);
					cm.sendOk("点播 #b"+Bgm[selection][1]+" - "+Bgm[selection][2]+"#k 成功。");
				}else{
					cm.sendOk("你今日已经不能点播了哦。");
				}
                cm.dispose();
                break;
        }
    }
}

