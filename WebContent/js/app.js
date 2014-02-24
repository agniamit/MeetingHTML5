var filename;
var recorderApp = angular.module('recorder', [ ]);

recorderApp.controller('RecorderController', [ '$scope' , function($scope) {
	$scope.stream = null;
	$scope.recording = false;
	$scope.encoder = null;
	$scope.ws = null;
	$scope.input = null;
	$scope.node = null;
	$scope.samplerate = 22050;
	$scope.samplerates = [ 8000, 11025, 12000, 16000, 22050, 24000, 32000, 44100, 48000 ];
	$scope.bitrate = 64;
	$scope.bitrates = [ 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 192, 224, 256, 320 ];
	$scope.recordButtonStyle = "red-btn";

	$scope.startRecording = function() {
		alert("hiiiiiiiiiiii");
		if ($scope.recording)
			return;
		console.log('start recording');
		$scope.encoder = new Worker('js/encoder.js');
		console.log('initializing encoder with samplerate = ' + $scope.samplerate + ' and bitrate = ' + $scope.bitrate);
		$scope.encoder.postMessage({ cmd: 'init', config: { samplerate: $scope.samplerate, bitrate: $scope.bitrate } });

		$scope.encoder.onmessage = function(e) {
			$scope.ws.send(e.data.buf);
			if (e.data.cmd == 'end') {
				$scope.ws.close();
				$scope.ws = null;
				$scope.encoder.terminate();
				$scope.encoder = null;
			}
		};
		var loc = window.location.host;
		loc = loc.substring(0,loc.indexOf(':'));
		console.log("loc::::"+loc);
		filename = "recording"+ Math.round(Math.random()*1000);
		alert("filename"+filename);
		$scope.ws = new WebSocket("ws://192.168.1.4:8000/ws/audio?recording="+filename);

		$scope.ws.onopen = function() {
			
			$scope.ws.send(filename);
			navigator.webkitGetUserMedia({ video: false, audio: true }, $scope.gotUserMedia, $scope.userMediaFailed);
		};
	};

	$scope.userMediaFailed = function(code) {
		console.log('grabbing microphone failed: ' + code);
	};

	$scope.startmeeting = function(code) {

		console.log('startmeeting ---------------------------');

	};

	$scope.gotUserMedia = function(localMediaStream) {
		$scope.recording = true;
		$scope.recordButtonStyle = '';

		console.log('success grabbing microphone');
		$scope.stream = localMediaStream;

		var audio_context = new window.webkitAudioContext();

		$scope.input = audio_context.createMediaStreamSource($scope.stream);
		$scope.node = $scope.input.context.createJavaScriptNode(4096, 1, 1);

		console.log('sampleRate: ' + $scope.input.context.sampleRate);

		$scope.node.onaudioprocess = function(e) {
			if (!$scope.recording)
				return;
			var channelLeft = e.inputBuffer.getChannelData(0);
			$scope.encoder.postMessage({ cmd: 'encode', buf: channelLeft });
		};

		$scope.input.connect($scope.node);
		$scope.node.connect(audio_context.destination);

		$scope.$apply();
	};

	$scope.stopRecording = function() {
		console.log("filename::::::::::"+filename);
		if (!$scope.recording) {
			return;
		}
		$scope.recordButtonStyle = "red-btn";
		console.log('stop recording');
		$scope.stream.stop();
		$scope.recording = false;
		$scope.encoder.postMessage({ cmd: 'finish' });

		$scope.input.disconnect();
		$scope.node.disconnect();
		$scope.input = $scope.node = null;
	};

}]);