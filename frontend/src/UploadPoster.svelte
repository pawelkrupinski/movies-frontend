<script>
import "@uppy/core/dist/style.css";
import "@uppy/dashboard/dist/style.css";
import Uppy from '@uppy/core'
import FileInput from '@uppy/file-input'
import XHRUpload from '@uppy/xhr-upload'
import Dashboard from '@uppy/dashboard'

export let movieId;
export let posterUpdated = () => {}

function startUppy() {
    const uppy = Uppy()
    
    uppy.use(FileInput, {
      target: '#file-input',
      pretty: true,
      inputName: 'files',
      autoProceed: true,
      replaceTargetContent: true
    })
    .use(XHRUpload, { endpoint: 'http://localhost:8000/poster/' + movieId })
    
    uppy.on('file-added', (file) => {
      uppy.upload()
    })
    
    uppy.on('complete', (result) => {
      posterUpdated()
    })
}

</script>

<div>
    Upload poster: <br/>
<!--    <div id="file-input" use:startUppy />-->
    <div id="file-input" />
</div>