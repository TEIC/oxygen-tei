<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:dbk="http://docbook.org/ns/docbook"
                xmlns:rng="http://relaxng.org/ns/structure/1.0"
                xmlns:tei="http://www.tei-c.org/ns/1.0"
                xmlns:teix="http://www.tei-c.org/ns/Examples"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:a="http://relaxng.org/ns/compatibility/annotations/1.0"
                xmlns:html="http://www.w3.org/1999/xhtml"

                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="xlink dbk rng tei teix xhtml a html xs xsl"
                version="2.0">
   <xsl:import href="../../../latex2/tei.xsl"/>
   <xsl:import href="../../../common2/msdescription.xsl"/>

  <doc xmlns="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet" type="stylesheet">
      <desc>

         <p> This library is free software; you can redistribute it and/or
      modify it under the terms of the GNU Lesser General Public License as
      published by the Free Software Foundation; either version 2.1 of the
      License, or (at your option) any later version. This library is
      distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
      without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
      PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
      details. You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the Free Software
      Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA </p>
         <p>Author: See AUTHORS</p>
         <p>Id: $Id: to.xsl 7953 2010-08-12 21:41:00Z rahtz $</p>
         <p>Copyright: 2008, TEI Consortium</p>
      </desc>
   </doc>

  
   <xsl:param name="reencode">false</xsl:param>
   <xsl:param name="numberBackHeadings">true</xsl:param>
   <xsl:param name="numberFrontHeadings">true</xsl:param>
   <xsl:param name="spaceCharacter">\hspace*{1em}</xsl:param>
   <xsl:param name="classParameters">11pt,twoside</xsl:param>
   <xsl:param name="startNamespace"/>
   <xsl:param name="tocNumberSuffix">.\ </xsl:param>
   <xsl:param name="numberSpacer">\ </xsl:param>
   <xsl:param name="parSkip">3pt</xsl:param>
   <xsl:param name="parIndent">3pt</xsl:param>
   <xsl:variable name="docClass">article</xsl:variable>
   <xsl:template name="latexPreambleHook">
\usepackage{makeidx}
\makeindex
\defaultfontfeatures{Scale=MatchLowercase}
%\setromanfont{DejaVu Serif}
%\setsansfont{DejaVu Sans}
\setmonofont{DejaVu Sans Mono}
%\setmonofont[Scale=0.9]{Lucida Sans Typewriter}
%\setsansfont[Scale=0.85]{Lucida Sans}
%\setromanfont{Times New Roman}
\setromanfont{Minion Pro}
%\setmonofont{CourierStd}
\setsansfont{Myriad Pro}
\setlength{\headheight}{14pt}
</xsl:template>


   <xsl:template name="latexBegin">
      <xsl:text>\makeatletter
\thispagestyle{plain}</xsl:text>
      <xsl:if test="not(tei:text/tei:front/tei:titlePage)">
         <xsl:call-template name="printTitleAndLogo"/>
      </xsl:if>
      <xsl:text>\markright{\@title}%
\markboth{\@title}{\@author}%
\fvset{frame=single,numberblanklines=false,xleftmargin=5mm,xrightmargin=5mm}
\fancyhf{} 
\setlength{\headheight}{14pt}
\fancyhead[LE]{\bfseries\leftmark} 
\fancyhead[RO]{\bfseries\rightmark} 
\fancyfoot[RO]{}
\fancyfoot[CO]{\thepage}
\fancyfoot[LO]{}
\fancyfoot[LE]{}
\fancyfoot[CE]{\thepage}
\fancyfoot[RE]{}
\hypersetup{linkbordercolor=0.75 0.75 0.75,urlbordercolor=0.75 0.75 0.75,bookmarksnumbered=true,letterpaper}
\def\l@section{\@dottedtocline{1}{3em}{2.3em}}
\def\l@subsection{\@dottedtocline{2}{4em}{3.2em}}
\def\l@subsubsection{\@dottedtocline{3}{5em}{4.1em}}
\def\l@paragraph{\@dottedtocline{4}{6em}{6em}}
\def\l@subparagraph{\@dottedtocline{5}{7em}{6em}}
\def\@pnumwidth{3em}
\setcounter{tocdepth}{2}
\def\tableofcontents{
\clearpage
\pdfbookmark[0]{Table of Contents}{TOC}
\hypertarget{TOC}{}
\section*{\contentsname}\@starttoc{toc}}
\fancypagestyle{plain}{\fancyhead{}\renewcommand{\headrulewidth}{0pt}}
\def\chaptermark#1{\markboth {\thechapter. \ #1}{}}
\def\sectionmark#1{\markright { \ifnum \c@secnumdepth &gt;\z@
          \thesection. \ %
        \fi
	#1}}
\def\egxmlcite#1{\raisebox{12pt}[0pt][0pt]{\parbox{.95\textwidth}{\raggedleft #1}}}
\def\oddindex#1{{\bfseries\hyperpage{#1}}}
\def\exampleindex#1{{\itshape\hyperpage{#1}}}
\def\mainexampleindex#1{{\bfseries\itshape\hyperpage{#1}}}
\setlength{\leftmargini}{2\parindent}%
\renewcommand{\@listI}{%
   \setlength{\leftmargin}{\leftmargini}%
   \setlength{\topsep}{\medskipamount}%
   \setlength{\itemsep}{0pt}%
   \setlength{\listparindent}{1em}%
   \setlength{\rightskip}{1em}%
}
\renewcommand\normalsize{\@setfontsize\normalsize{10}{12}%
  \abovedisplayskip 10\p@ plus2\p@ minus5\p@
  \belowdisplayskip \abovedisplayskip
  \abovedisplayshortskip  \z@ plus3\p@
  \belowdisplayshortskip  6\p@ plus3\p@ minus3\p@
  \let\@listi\@listI
}
\renewcommand\small{\@setfontsize\small{9pt}{11pt}%
   \abovedisplayskip 8.5\p@ plus3\p@ minus4\p@
   \belowdisplayskip \abovedisplayskip
   \abovedisplayshortskip \z@ plus2\p@
   \belowdisplayshortskip 4\p@ plus2\p@ minus2\p@
   \def\@listi{\leftmargin\leftmargini
               \topsep 2\p@ plus1\p@ minus1\p@
               \parsep 2\p@ plus\p@ minus\p@
               \itemsep 1pt}
}
\renewcommand\footnotesize{\@setfontsize\footnotesize{8}{9.5}%
  \abovedisplayskip 6\p@ plus2\p@ minus4\p@
  \belowdisplayskip \abovedisplayskip
  \abovedisplayshortskip \z@ plus\p@
  \belowdisplayshortskip 3\p@ plus\p@ minus2\p@
  \def\@listi{\leftmargin\leftmargini
              \topsep 2\p@ plus\p@ minus\p@
              \parsep 2\p@ plus\p@ minus\p@
              \itemsep \parsep}
}
\renewcommand\scriptsize{\@setfontsize\scriptsize{7}{8}}
\renewcommand\tiny{\@setfontsize\tiny{5}{6}}
\renewcommand\large{\@setfontsize\large{12}{14.4}}
\renewcommand\Large{\@setfontsize\Large{14.4}{18}}
\renewcommand\LARGE{\@setfontsize\LARGE{17.28}{22}}
\renewcommand\huge{\@setfontsize\huge{20.74}{25}}
\renewcommand\Huge{\@setfontsize\Huge\@xxvpt{30}}
%\parskip3pt
%\parindent0em
% for refdocs
\renewenvironment{itemize}{%
  \advance\@itemdepth \@ne
  \edef\@itemitem{labelitem\romannumeral\the\@itemdepth}%
  \begin{list}{\csname\@itemitem\endcsname}
  {%
   \setlength{\leftmargin}{\parindent}%
   \setlength{\labelwidth}{.7\parindent}%
   \setlength{\topsep}{2pt}%
   \setlength{\itemsep}{2pt}%
   \setlength{\itemindent}{2pt}%
   \setlength{\parskip}{0pt}%
   \setlength{\parsep}{2pt}%
   \def\makelabel##1{\hfil##1\hfil}}%
  }
  {\end{list}}
\catcode`說=\active \def說{{\fontspec{AR PL ZenKai Uni}\char35498}}
\catcode`説=\active \def説{{\fontspec{Kochi Mincho}\char35500}}
\catcode`人=\active \def人{{\fontspec{Kochi Mincho}\char20154}}
\catcode`⁊=\active \def⁊{{\fontspec{Junicode}\char8266}} 
\catcode`Å=\active \defÅ{{\fontspec{DejaVu Serif}\char8491}} 
\catcode`⁻=\active \def⁻{\textsuperscript{-}}
\catcode` =\active \def {\,}
\fancyhfoffset[LO,LE]{2em}
\renewcommand\section{\@startsection {section}{1}{-2em}%
     {-1.75ex \@plus -0.5ex \@minus -.2ex}%
     {0.5ex \@plus .2ex}%
     {\reset@font\Large\bfseries\sffamily}}
\renewcommand\subsection{\@startsection{subsection}{2}{-2em}%
     {-1.75ex\@plus -0.5ex \@minus- .2ex}%
     {0.5ex \@plus .2ex}%
     {\reset@font\Large\sffamily}}
\makeatother </xsl:text>
      <xsl:call-template name="beginDocumentHook"/>
   </xsl:template>

   <xsl:param name="latexGeometryOptions">twoside,letterpaper,lmargin=1in,rmargin=1in,tmargin=1in,bmargin=1in</xsl:param>

   <xsl:template match="tei:byline"/>
   <xsl:template match="tei:titlePage/tei:note"/>

   <xsl:template match="tei:list">
      <xsl:if test="parent::tei:item">\mbox{}\\[-10pt] </xsl:if>
      <xsl:apply-imports/>
   </xsl:template>

   <xsl:template name="lineBreak">
      <xsl:param name="id"/>
      <xsl:text>\mbox{}\newline 
</xsl:text>
   </xsl:template>

    <xsl:template name="msSection">
      <xsl:param name="level"/>
      <xsl:param name="heading"/>
      <xsl:param name="implicitBlock">false</xsl:param>
      <xsl:text>
</xsl:text>
      <xsl:choose>
	        <xsl:when test="$level=1">\section</xsl:when>
	        <xsl:when test="$level=2">\subsection</xsl:when>
	        <xsl:when test="$level=3">\subsubsection</xsl:when>
	        <xsl:when test="$level=4">\paragraph</xsl:when>
      </xsl:choose>
	     <xsl:text>{</xsl:text>
	     <xsl:value-of select="$heading"/>
	     <xsl:text>}
</xsl:text>
      <xsl:choose>
	        <xsl:when test="$implicitBlock='true'">
\par
	    <xsl:apply-templates/>
\par
	</xsl:when>
	        <xsl:when test="*">
	           <xsl:apply-templates/>
	        </xsl:when>
	        <xsl:otherwise>
\par
	    <xsl:apply-templates/>
\par
	</xsl:otherwise>
      </xsl:choose>
    </xsl:template>
    
    <xsl:template name="msInline">
      <xsl:param name="before"/>
      <xsl:param name="style"/>
      <xsl:param name="after"/>
      <xsl:value-of select="$before"/>
	     <xsl:choose>
	        <xsl:when test="$style='italic'">
	           <xsl:text>\textit{</xsl:text>
	           <xsl:value-of select="normalize-space(.)"/>
	           <xsl:text>}</xsl:text>
	        </xsl:when>
	        <xsl:when test="$style='bold'">
	           <xsl:text>\textbf{</xsl:text>
	           <xsl:value-of select="normalize-space(.)"/>
	           <xsl:text>}</xsl:text>	    
	        </xsl:when>
	        <xsl:otherwise>
	           <xsl:value-of select="normalize-space(.)"/>
	        </xsl:otherwise>
	     </xsl:choose>
      <xsl:value-of select="$after"/>
    </xsl:template>


    <xsl:template name="msBlock">
      <xsl:param name="style"/>
      <xsl:text>\par </xsl:text>
      <xsl:apply-templates/>
      <xsl:text>\par </xsl:text>
    </xsl:template>


    <xsl:template name="msLabelled">
      <xsl:param name="before"/>
      <xsl:text>\textit{</xsl:text>
      <xsl:value-of select="$before"/>
      <xsl:text>}: </xsl:text>
      <xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="tei:teiHeader">
      <xsl:choose>
	        <xsl:when test="not(parent::tei:*)">
	           <xsl:call-template name="mainDocument"/>
	        </xsl:when>
	        <xsl:otherwise>
	           <xsl:apply-templates select="tei:fileDesc"/>
	        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>
    
    <xsl:template match="tei:fileDesc">
      <xsl:apply-templates select="tei:sourceDesc/tei:msDesc"/>
    </xsl:template>

</xsl:stylesheet>