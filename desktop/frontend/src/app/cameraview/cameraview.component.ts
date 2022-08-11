import { Component, OnInit } from '@angular/core';
import {webSocket} from 'rxjs/webSocket'; 
import { DomSanitizer } from '@angular/platform-browser';
import { BackendService } from '../../services/backend.service';
import { HttpClient } from '@angular/common/http';
import {Router} from '@angular/router';
import { DashboardComponent } from '../dashboard/dashboard.component';

@Component({
  selector: 'app-cameraview',
  templateUrl: './cameraview.component.html',
  styleUrls: ['./cameraview.component.css']
})
export class CameraviewComponent implements OnInit {

  dashboardObj;

  constructor(private httpClient:HttpClient, private sanitizer:DomSanitizer, private service:BackendService, private route:Router) { }

  ngOnInit() 
  {
    this.dashboardObj = new DashboardComponent(this.httpClient, this. sanitizer, this.service, this.route);
    this.dashboardObj.webSocketFeed();
  }

}
