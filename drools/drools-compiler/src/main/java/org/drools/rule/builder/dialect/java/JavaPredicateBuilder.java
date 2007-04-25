package org.drools.rule.builder.dialect.java;

import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.Declaration;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.util.StringUtils;

public class JavaPredicateBuilder
    implements
    PredicateBuilder {

    public void build(final RuleBuildContext context,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final PredicateConstraint predicateConstraint,
                      final PredicateDescr predicateDescr) {
        // generate 
        // generate Invoker
        final String className = "predicate" + context.getNextId();
        predicateDescr.setClassMethodName( className );
        
        JavaDialect dialect = (JavaDialect) context.getDialect();

        StringTemplate st = dialect.getRuleGroup().getInstanceOf( "predicateMethod" );

        dialect.setStringTemplateAttributes( context,
                                           st,
                                           previousDeclarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        final String[] localDeclarationTypes = new String[localDeclarations.length];
        for ( int i = 0, size = localDeclarations.length; i < size; i++ ) {
            localDeclarationTypes[i] = ( (JavaDialect) context.getDialect()).getTypeFixer().fix( localDeclarations[i] );
        }

        st.setAttribute( "localDeclarations",
                         localDeclarations );
        st.setAttribute( "localDeclarationTypes",
                         localDeclarationTypes );

        st.setAttribute( "methodName",
                         className );

        final String predicateText = (String) predicateDescr.getContent();

        st.setAttribute( "text",
                         predicateText );

        context.getMethods().add( st.toString() );

        st = dialect.getInvokerGroup().getInstanceOf( "predicateInvoker" );

        st.setAttribute( "package",
                         context.getPkg().getName() );
        st.setAttribute( "ruleClassName",
                         StringUtils.ucFirst( context.getRuleDescr().getClassName() ) );
        st.setAttribute( "invokerClassName",
                         context.getRuleDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        dialect.setStringTemplateAttributes( context,
                                           st,
                                           previousDeclarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        st.setAttribute( "localDeclarations",
                         localDeclarations );
        st.setAttribute( "localDeclarationTypes",
                         localDeclarationTypes );

        st.setAttribute( "hashCode",
                         predicateText.hashCode() );

        final String invokerClassName = context.getPkg().getName() + "." + context.getRuleDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker";
        context.getInvokers().put( invokerClassName,
                                   st.toString() );
        context.getInvokerLookups().put( invokerClassName,
                                         predicateConstraint );
        context.getDescrLookups().put( invokerClassName,
                                       predicateDescr );
    }

}
