<div class="detailed-post-view" id="ElementList">
    {{#each mData}}
    <div class = "post-view" data-value="{{this.mId}}"> 
        <div class = "title">
            {{this.mSubject}}
        </div>
        <div class = "message">
            {{this.mMessage}}
        </div>
        
        <br/>

        <div class = "buttons">
            <button id = "edit-post-btn" class = "edit-btn" data-value= "{{this.mId}}">
                <span class="glyphicon glyphicon-edit"></span>
                Edit
            </button>
            <button id = "delete-post-btn" class = "delete-btn" data-value="{{this.mId}}">
                <span class="glyphicon glyphicon-remove"></span>
                Delete
            </button>
            <button id = "upvote-post-btn" class= "my-upvote" data-value = "{{this.mId}}"
                data-upvotestate = "false">
                <span class = "glyphicon glyphicon-thumbs-up"></span>
                {{this.mUpvotes}}
            </button>
            <button id = "downvote-post-btn" class = "my-downvote" data-value = "{{this.mId}}"
                data-downvotestate = "false">
                <span class = "glyphicon glyphicon-thumbs-down"></span>
                {{this.mDownvotes}}
            </buttons>
        </div>
    </div>
    {{/each}}
</div>