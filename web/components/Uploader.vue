<template>
  <div class="flex flex-col items-center p-6 m-4 bg-gray rounded-2xl shadow w-screen max-w-screen-md">
    <h2 class="text-xl font-bold">Upload Minecraft Schematic</h2>
    <div v-if="error" class="flex bg-red-500 font-bold mt-4 px-4 py-2 w-full justify-center rounded-lg shadow" >
      {{ error }}
    </div>
    <div class="w-full p-4">
      <div class="flex flex-col items-center" v-if="state === UploadState.ENTER_CODE">
        <p class="my-2">Enter an in-game Upload Code (Get one with <code>/schematicupload)</code>.</p>
        <div class="my-4">
          <input type="text" class="w-full p-2 border-2 border-lite-gray rounded-lg shadow" placeholder="Upload Code" v-model="uploadCode">
          <button class="w-full p-2 mt-4 font-semibold text-black bg-accent rounded-lg shadow" @click="codeEntered">Next</button>
        </div>
      </div>
      <div class="flex flex-col items-center" v-if="state === UploadState.UPLOAD_FILE">
        <p class="my-2">Select the schematic file to upload.</p>
        <FileInput @schematicSelected="schematicSelected"/>
      </div>
      <div class="flex flex-col items-center" v-if="state === UploadState.PREVIEW">
        <p class="my-2">Does the schematic look correct? If so, click "Upload."</p>
        <SchematicPreview :file="selectedFile"/>
        <button class="w-full p-2 mt-4 font-semibold text-black bg-accent rounded-lg shadow" @click="codeEntered">Upload</button>
      </div>
    </div>
    <ProgressNotches class="mt-2" :state="state"/>
  </div>
</template>

<script setup lang="ts">
enum UploadState {
  ENTER_CODE = 0,
  UPLOAD_FILE = 1,
  PREVIEW = 2,
  DONE = 3
}

const state = ref<UploadState>(UploadState.ENTER_CODE);
const error = ref<string>("");
const uploadCode = ref<string>("");
const selectedFile = ref<File | null>(null);

const codeEntered = () => {
  console.log(uploadCode);
  if (uploadCode.value === "") {
    error.value = "Please enter an upload code.";
    return;
  }
  error.value = "";
  state.value = UploadState.UPLOAD_FILE;
}

const schematicSelected = (file: File) => {
  console.log(file);
  selectedFile.value = file;
  error.value = "";
  state.value = UploadState.PREVIEW;
}
</script>