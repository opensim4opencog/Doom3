package org.opencyc.cyclobject.impl;

import org.opencyc.cyclobject.*;

/*****************************************************************************
 * KB comment for #$CycLClosedNonAtomicTerm as of 2002/05/07:<p>
 *
 * The collection of closed, non-atomic denotational terms of the CycL language.
 * Each instance of #$CycLClosedNonAtomicTerm is a CycL term that contains no
 * free variables, is constructible from other CycL terms via the syntax of
 * CycL, and can have a denotatum (this last requirement excludes closed CycL
 * sentences from #$CycLClosedNonAtomicTerm).  Examples: `(#$JuvenileFn
 * #$Platypus)', `(#$JuvenileFn #$isa #$genls)', and `(#$TheSetOf ?X
 * (#$objectHasColor ?X #$GreenColor))'.  Non-example: `(#$JuvenileFn ?X)'.<p>
 *
 * @version $Id: CycLClosedNonAtomicTermImpl.java,v 1.4 2002/05/07 20:04:01 jantos Exp $
 * @author Tony Brusseau, Steve Reed
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://sf.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/
public abstract class CycLClosedNonAtomicTermImpl 
  extends CycLNonAtomicTermClosedFunctorImpl
  implements CycLClosedNonAtomicTerm, CycLClosedDenotationalTerm {
  
}