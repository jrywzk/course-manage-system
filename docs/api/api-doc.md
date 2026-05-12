# sms接口文档


**简介**:sms接口文档


**HOST**:localhost:9090


**联系人**:


**Version**:1.0


**接口路径**:/v2/api-docs


[TOC]






# admin-controller


## deleteByAdminId


**接口地址**:`/api/admin/deleteByAdminId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|adminId|adminId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## register


**接口地址**:`/api/admin/register`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "name": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|admin|admin|body|true|Admin|Admin|
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||
|password|password|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectByAdminName


**接口地址**:`/api/admin/selectByAdminName`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|adminName|adminName|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«Admin»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|Admin|
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"id": 0,
			"name": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## updateByAdminId


**接口地址**:`/api/admin/updateAdmin`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "name": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|admin|admin|body|true|Admin|Admin|
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||
|adminId|adminId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


# course-controller


## deleteByCourseIdAndTeacherId


**接口地址**:`/api/course/deleteByCourseIdAndTeacherId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId|courseId|query|true|integer(int32)||
|teacherId|teacherId|body|false|integer||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## insertCourse


**接口地址**:`/api/course/insertCourse`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "credit": 0,
  "id": 0,
  "name": "",
  "studentLimit": 0,
  "teacherId": 0,
  "term": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|course|course|body|true|Course|Course|
|&emsp;&emsp;credit|||false|integer(int32)||
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||
|&emsp;&emsp;studentLimit|||false|integer(int32)||
|&emsp;&emsp;teacherId|||false|integer(int32)||
|&emsp;&emsp;term|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectAll


**接口地址**:`/api/course/selectAll`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«CourseVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|CourseVo|
|&emsp;&emsp;credit||integer(int32)||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;studentLimit||integer(int32)||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|&emsp;&emsp;term||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"credit": 0,
			"id": 0,
			"name": "",
			"studentLimit": 0,
			"teacherId": 0,
			"teacherName": "",
			"term": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectByCourseId


**接口地址**:`/api/course/selectByCourseId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId|courseId|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«CourseVo»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||CourseVo|CourseVo|
|&emsp;&emsp;credit||integer(int32)||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;studentLimit||integer(int32)||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|&emsp;&emsp;term||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": {
		"credit": 0,
		"id": 0,
		"name": "",
		"studentLimit": 0,
		"teacherId": 0,
		"teacherName": "",
		"term": ""
	},
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectCourseByName


**接口地址**:`/api/course/selectByCourseName`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseName|courseName|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«CourseVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|CourseVo|
|&emsp;&emsp;credit||integer(int32)||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;studentLimit||integer(int32)||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|&emsp;&emsp;term||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"credit": 0,
			"id": 0,
			"name": "",
			"studentLimit": 0,
			"teacherId": 0,
			"teacherName": "",
			"term": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectByTeacherId


**接口地址**:`/api/course/selectByTeacherId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacherId|teacherId|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«CourseVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|CourseVo|
|&emsp;&emsp;credit||integer(int32)||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;studentLimit||integer(int32)||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|&emsp;&emsp;term||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"credit": 0,
			"id": 0,
			"name": "",
			"studentLimit": 0,
			"teacherId": 0,
			"teacherName": "",
			"term": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectByTeacherName


**接口地址**:`/api/course/selectByTeacherName`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacherName|teacherName|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«CourseVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|CourseVo|
|&emsp;&emsp;credit||integer(int32)||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|&emsp;&emsp;studentLimit||integer(int32)||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|&emsp;&emsp;term||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"credit": 0,
			"id": 0,
			"name": "",
			"studentLimit": 0,
			"teacherId": 0,
			"teacherName": "",
			"term": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## updateCourse


**接口地址**:`/api/course/updateCourse`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "credit": 0,
  "id": 0,
  "name": "",
  "studentLimit": 0,
  "teacherId": 0,
  "term": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|course|course|body|true|Course|Course|
|&emsp;&emsp;credit|||false|integer(int32)||
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||
|&emsp;&emsp;studentLimit|||false|integer(int32)||
|&emsp;&emsp;teacherId|||false|integer(int32)||
|&emsp;&emsp;term|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


# login-controller


## login


**接口地址**:`/api/login`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|id|query|true|integer(int32)||
|password|password|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


# score-controller


## deleteScore


**接口地址**:`/api/score/deleteByCourseIdAndStudentIdAndTeacherId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId|courseId|query|false|integer(int32)||
|studentId|studentId|query|false|integer(int32)||
|teacherId|teacherId|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## insertScore


**接口地址**:`/api/score/insert`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "courseId": 0,
  "id": 0,
  "score": 0,
  "studentId": 0,
  "teacherId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|score|score|body|true|Score|Score|
|&emsp;&emsp;courseId|||false|integer(int32)||
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;score|||false|integer(int32)||
|&emsp;&emsp;studentId|||false|integer(int32)||
|&emsp;&emsp;teacherId|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectScoreByCourseId


**接口地址**:`/api/score/selectByCourseId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId|courseId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«ScoreVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|ScoreVo|
|&emsp;&emsp;courseId||integer(int32)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;score||integer(int32)||
|&emsp;&emsp;studentId||integer(int32)||
|&emsp;&emsp;studentName||string||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"courseId": 0,
			"courseName": "",
			"id": 0,
			"score": 0,
			"studentId": 0,
			"studentName": "",
			"teacherId": 0,
			"teacherName": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectScoreByCourseName


**接口地址**:`/api/score/selectByCourseName`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseName|courseName|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«ScoreVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|ScoreVo|
|&emsp;&emsp;courseId||integer(int32)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;score||integer(int32)||
|&emsp;&emsp;studentId||integer(int32)||
|&emsp;&emsp;studentName||string||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"courseId": 0,
			"courseName": "",
			"id": 0,
			"score": 0,
			"studentId": 0,
			"studentName": "",
			"teacherId": 0,
			"teacherName": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectScoreByStudentId


**接口地址**:`/api/score/selectByStudentId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|studentId|studentId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«ScoreVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|ScoreVo|
|&emsp;&emsp;courseId||integer(int32)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;score||integer(int32)||
|&emsp;&emsp;studentId||integer(int32)||
|&emsp;&emsp;studentName||string||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"courseId": 0,
			"courseName": "",
			"id": 0,
			"score": 0,
			"studentId": 0,
			"studentName": "",
			"teacherId": 0,
			"teacherName": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectScoreByStudentIdAndTerm


**接口地址**:`/api/score/selectByStudentIdAndTerm`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|studentId|studentId|query|true|integer(int32)||
|term|term|query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«ScoreVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|ScoreVo|
|&emsp;&emsp;courseId||integer(int32)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;score||integer(int32)||
|&emsp;&emsp;studentId||integer(int32)||
|&emsp;&emsp;studentName||string||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"courseId": 0,
			"courseName": "",
			"id": 0,
			"score": 0,
			"studentId": 0,
			"studentName": "",
			"teacherId": 0,
			"teacherName": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectScoreByTeacherId


**接口地址**:`/api/score/selectByTeacherId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacherId|teacherId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«ScoreVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|ScoreVo|
|&emsp;&emsp;courseId||integer(int32)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;score||integer(int32)||
|&emsp;&emsp;studentId||integer(int32)||
|&emsp;&emsp;studentName||string||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"courseId": 0,
			"courseName": "",
			"id": 0,
			"score": 0,
			"studentId": 0,
			"studentName": "",
			"teacherId": 0,
			"teacherName": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectScoreByTeacherIdAndCourseId


**接口地址**:`/api/score/selectByTeacherIdAndCourseId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId|courseId|query|true|integer(int32)||
|teacherId|teacherId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«ScoreVo»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|ScoreVo|
|&emsp;&emsp;courseId||integer(int32)||
|&emsp;&emsp;courseName||string||
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;score||integer(int32)||
|&emsp;&emsp;studentId||integer(int32)||
|&emsp;&emsp;studentName||string||
|&emsp;&emsp;teacherId||integer(int32)||
|&emsp;&emsp;teacherName||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"courseId": 0,
			"courseName": "",
			"id": 0,
			"score": 0,
			"studentId": 0,
			"studentName": "",
			"teacherId": 0,
			"teacherName": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


# student-controller


## deleteByStudentId


**接口地址**:`/api/student/deleteByStudentId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|studentId|studentId|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## addStudent


**接口地址**:`/api/student/register`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "name": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|student|student|body|true|Student|Student|
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||
|password|password|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectByStudentName


**接口地址**:`/api/student/selectByStudentName`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|studentName|studentName|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«Student»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|Student|
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"id": 0,
			"name": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## updateStudent


**接口地址**:`/api/student/update`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "name": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|student|student|body|true|Student|Student|
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


# teacher-controller


## deleteByTeacherId


**接口地址**:`/api/teacher/deleteByTeacherId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacherId|teacherId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## register


**接口地址**:`/api/teacher/register`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "name": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacher|teacher|body|true|Teacher|Teacher|
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||
|password|password|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectById


**接口地址**:`/api/teacher/selectByTeacherId`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacherId|teacherId|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«Teacher»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||Teacher|Teacher|
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": {
		"id": 0,
		"name": ""
	},
	"map": {},
	"msg": "",
	"status": 0
}
```


## selectByName


**接口地址**:`/api/teacher/selectByTeacherName`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacherName|teacherName|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«List«Teacher»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||array|Teacher|
|&emsp;&emsp;id||integer(int32)||
|&emsp;&emsp;name||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": [
		{
			"id": 0,
			"name": ""
		}
	],
	"map": {},
	"msg": "",
	"status": 0
}
```


## update


**接口地址**:`/api/teacher/update`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "name": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|teacher|teacher|body|true|Teacher|Teacher|
|&emsp;&emsp;id|||false|integer(int32)||
|&emsp;&emsp;name|||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


# test-controller


## test


**接口地址**:`/api/test`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# user-controller


## deleteById


**接口地址**:`/api/user/deleteById`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId|userId|query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## insert


**接口地址**:`/api/user/insert`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||query|false|integer(int32)||
|password||query|false|string||
|role||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## login


**接口地址**:`/api/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|id|query|false|integer(int32)||
|password|password|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```


## update


**接口地址**:`/api/user/update`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||query|false|integer(int32)||
|password||query|false|string||
|role||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|R«string»|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|data||string||
|map||object||
|msg||string||
|status||integer(int32)|integer(int32)|


**响应示例**:
```javascript
{
	"data": "",
	"map": {},
	"msg": "",
	"status": 0
}
```