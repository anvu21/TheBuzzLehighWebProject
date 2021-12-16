# Web Front-End

## Front-end Role
- Phase 1: Michelle Li

## Developer guide

### Major UI Components
- AppFrontPage
- ElementList
- EditEntryForm
- NewEntryForm

### Deployment
When you deploy, `index.html` under the root directory will be copied into the
resource folder. `app.ts` will be linked with the scripts under `ts` directory
and be compiled into `app.js` in resource folder. `apptest.ts` will be compiled
into `apptest.js`. All `.hb` files in the hb folder will be compiled into `templates.js`. 
All `.css` files in the css folder will be concatinate into `app.css`. All of 
them will be put in backend's resource folder to be packaged into its `jar` file, 
which will then get deployed onto `heroku` server and run. (Please refer to the 
content of`deploy.sh` for more details about deployment).

### Sending ajax request to backend
Here is an example of using the `$.ajax` function:

```javascript
	$.ajax({
		type: "GET",
		url: backendUrl + "/messages",
		dataType: "json",
		success: function(res: any){
		console.log("[ajax] All Posts Response: " + JSON.stringify(res));
		ElementList.update(res);
		}
	});
```


