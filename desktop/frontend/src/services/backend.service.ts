import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(private http:HttpClient, private httpClient:HttpClient) { 
    console.log("In Backend Service");
  }

  getIpAddPort()
  {
    return this.http.get("http://localhost:8000/getIpAddPort");
  }
}
