<#-- 数值属性 -->
<#assign x=field.dataType>
<#if ((x==-7)||(x==-6)||(x==5)||(x==4)||(x==-5)||(x==6)||(x==7)||(x==8)||(x==2)||(x==3)||(x==91)||(x==92)||(x==93))>
		public Criteria and${field.property1}IsNull() {
		    addCriterion("${field.columnName} is null");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}IsNotNull() {
		    addCriterion("${field.columnName} is not null");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}EqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} =", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}NotEqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} <>", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}GreaterThan(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} >", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}GreaterThanOrEqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} >=", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}LessThan(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} <", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}LessThanOrEqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} <=", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}In(List<${field.shorJavaClass}> values) {
		    addCriterion("${field.columnName} in", values, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}NotIn(List<${field.shorJavaClass}> values) {
		    addCriterion("${field.columnName} not in", values, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}Between(${field.shorJavaClass} value1, ${field.shorJavaClass} value2) {
		    addCriterion("${field.columnName} between", value1, value2, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}NotBetween(${field.shorJavaClass} value1, ${field.shorJavaClass} value2) {
		    addCriterion("${field.columnName} not between", value1, value2, "${field.property}");
		    return (Criteria) this;
		}
<#-- 字符串属性 -->
<#elseif ((x==1)||(x==12)||(x==-1))>
		public Criteria and${field.property1}IsNull() {
		    addCriterion("${field.columnName} is null");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}IsNotNull() {
		    addCriterion("${field.columnName} is not null");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}EqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} =", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}NotEqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} <>", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}GreaterThan(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} >", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}GreaterThanOrEqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} >=", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}LessThan(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} <", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}LessThanOrEqualTo(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} <=", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}Like(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} like", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}NotLike(${field.shorJavaClass} value) {
		    addCriterion("${field.columnName} not like", value, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}In(List<${field.shorJavaClass}> values) {
		    addCriterion("${field.columnName} in", values, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}NotIn(List<${field.shorJavaClass}> values) {
		    addCriterion("${field.columnName} not in", values, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}Between(${field.shorJavaClass} value1, ${field.shorJavaClass} value2) {
		    addCriterion("${field.columnName} between", value1, value2, "${field.property}");
		    return (Criteria) this;
		}
		
		public Criteria and${field.property1}NotBetween(${field.shorJavaClass} value1, ${field.shorJavaClass} value2) {
		    addCriterion("${field.columnName} not between", value1, value2, "${field.property}");
		    return (Criteria) this;
		}
</#if>