import { Component, OnInit } from '@angular/core';
import {webSocket} from 'rxjs/webSocket'; 
import { DomSanitizer } from '@angular/platform-browser';
import { BackendService } from '../../services/backend.service';
import { Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})

export class DashboardComponent implements OnInit {

  notValidIPFormat = true;
  notValidPortFormat = true;
  ipAdd;
  port;
  streamStarted = false;
  frameFromCamera: any;
  subscriber

  constructor(private httpClient:HttpClient, private sanitizer:DomSanitizer, private service:BackendService, private route:Router) {}


  ngOnInit() 
  {
    this.webSocketFeed();
    this.service.getIpAddPort().subscribe(data =>{
      this.ipAdd = data['server'];
      this.port = data['port'];

      if(this.ipAdd != null)
      {
        this.notValidIPFormat = false;
      }
      if(this.port != null)
      {
        this.notValidPortFormat = false;
      }
    });
  }

  webSocketFeed()
  {
    let wsIP="ws://127.0.0.1:8000/clientConnected";
  
    let ws = webSocket(wsIP);
    this.subscriber = ws.subscribe(
      async data =>{
        this.streamStarted = data['clientConnected'];
        this.frameFromCamera = 'data:image/jpg;base64,' + data['frame'];
        this.frameFromCamera = this.sanitizer.bypassSecurityTrustUrl(this.frameFromCamera);
        console.log(this.streamStarted);
        if(this.streamStarted === true)
        {
          await this.delay(2000);
          this.route.navigate(['/cameraview']);
        }
        if(this.streamStarted === false)
        {
          this.route.navigate(['/dashboard']);
        }
      },
    );
  }


  // Function for introducing delay in ms
  delay(delayInms) {
    return new Promise(resolve => {
      setTimeout(() => {
        resolve(2);
      }, delayInms);
    });
  }
}