(function(k){var a=new Hashtable();var f=["ae","au","ca","cn","eg","gb","hk","il","in","jp","sk","th","tw","us"];var b=["at","br","de","dk","es","gr","it","nl","pt","tr","vn"];var i=["cz","fi","fr","ru","se","pl"];var d=["ch"];var g=[[".",","],[",","."],[","," "],[".","'"]];var c=[f,b,i,d];function j(n,l,m){this.dec=n;this.group=l;this.neg=m}function h(){for(var l=0;l<c.length;l++){localeGroup=c[l];for(var m=0;m<localeGroup.length;m++){a.put(localeGroup[m],l)}}}function e(l){if(a.size()==0){h()}var q=".";var o=",";var p="-";var n=a.get(l);if(n){var m=g[n];if(m){q=m[0];o=m[1]}}return new j(q,o,p)}k.fn.formatNumber=function(l,m,n){return this.each(function(){if(m==null){m=true}if(n==null){n=true}var p;if(k(this).is(":input")){p=new String(k(this).val())}else{p=new String(k(this).text())}var o=k.formatNumber(p,l);if(m){if(k(this).is(":input")){k(this).val(o)}else{k(this).text(o)}}if(n){return o}})};k.formatNumber=function(q,w){var w=k.extend({},k.fn.formatNumber.defaults,w);var l=e(w.locale.toLowerCase());var n=l.dec;var u=l.group;var o=l.neg;var m="0#-,.";var t="";var s=false;for(var r=0;r<w.format.length;r++){if(m.indexOf(w.format.charAt(r))==-1){t=t+w.format.charAt(r)}else{if(r==0&&w.format.charAt(r)=="-"){s=true;continue}else{break}}}var v="";for(var r=w.format.length-1;r>=0;r--){if(m.indexOf(w.format.charAt(r))==-1){v=w.format.charAt(r)+v}else{break}}w.format=w.format.substring(t.length);w.format=w.format.substring(0,w.format.length-v.length);var p=new Number(q);return k._formatNumber(p,w,v,t,s)};k._formatNumber=function(m,q,n,G,s){var q=k.extend({},k.fn.formatNumber.defaults,q);var E=e(q.locale.toLowerCase());var D=E.dec;var v=E.group;var l=E.neg;var x=false;if(isNaN(m)){if(q.nanForceZero==true){m=0;x=true}else{return null}}if(n=="%"){m=m*100}var z="";if(q.format.indexOf(".")>-1){var F=D;var t=q.format.substring(q.format.lastIndexOf(".")+1);if(q.round==true){m=new Number(m.toFixed(t.length))}else{var K=m.toString();K=K.substring(0,K.lastIndexOf(".")+t.length+1);m=new Number(K)}var y=m%1;var A=new String(y.toFixed(t.length));A=A.substring(A.lastIndexOf(".")+1);for(var H=0;H<t.length;H++){if(t.charAt(H)=="#"&&A.charAt(H)!="0"){F+=A.charAt(H);continue}else{if(t.charAt(H)=="#"&&A.charAt(H)=="0"){var r=A.substring(H);if(r.match("[1-9]")){F+=A.charAt(H);continue}else{break}}else{if(t.charAt(H)=="0"){F+=A.charAt(H)}}}}z+=F}else{m=Math.round(m)}var u=Math.floor(m);if(m<0){u=Math.ceil(m)}var C="";if(q.format.indexOf(".")==-1){C=q.format}else{C=q.format.substring(0,q.format.indexOf("."))}var J="";if(!(u==0&&C.substr(C.length-1)=="#")||x){var w=new String(Math.abs(u));var p=9999;if(C.lastIndexOf(",")!=-1){p=C.length-C.lastIndexOf(",")-1}var o=0;for(var H=w.length-1;H>-1;H--){J=w.charAt(H)+J;o++;if(o==p&&H!=0){J=v+J;o=0}}if(C.length>J.length){var I=C.indexOf("0");if(I!=-1){var B=C.length-I;while(J.length<B){J="0"+J}}}}if(!J&&C.indexOf("0",C.length-1)!==-1){J="0"}z=J+z;if(m<0&&s&&G.length>0){G=l+G}else{if(m<0){z=l+z}}if(!q.decimalSeparatorAlwaysShown){if(z.lastIndexOf(D)==z.length-1){z=z.substring(0,z.length-1)}}z=G+z+n;return z};k.fn.parseNumber=function(l,m,o){if(m==null){m=true}if(o==null){o=true}var p;if(k(this).is(":input")){p=new String(k(this).val())}else{p=new String(k(this).text())}var n=k.parseNumber(p,l);if(n){if(m){if(k(this).is(":input")){k(this).val(n.toString())}else{k(this).text(n.toString())}}if(o){return n}}};k.parseNumber=function(r,v){var v=k.extend({},k.fn.parseNumber.defaults,v);var m=e(v.locale.toLowerCase());var o=m.dec;var t=m.group;var p=m.neg;var l="1234567890.-";while(r.indexOf(t)>-1){r=r.replace(t,"")}r=r.replace(o,".").replace(p,"-");var u="";var n=false;if(r.charAt(r.length-1)=="%"){n=true}for(var s=0;s<r.length;s++){if(l.indexOf(r.charAt(s))>-1){u=u+r.charAt(s)}}var q=new Number(u);if(n){q=q/100;q=q.toFixed(u.length-1)}return q};k.fn.parseNumber.defaults={locale:"us",decimalSeparatorAlwaysShown:false};k.fn.formatNumber.defaults={format:"#,###.00",locale:"us",decimalSeparatorAlwaysShown:false,nanForceZero:true,round:true};Number.prototype.toFixed=function(l){return $._roundNumber(this,l)};k._roundNumber=function(n,m){var l=Math.pow(10,m||0);var o=String(Math.round(n*l)/l);if(m>0){var p=o.indexOf(".");if(p==-1){o+=".";p=0}else{p=o.length-(p+1)}while(p<m){o+="0";p++}}return o}})(jQuery);