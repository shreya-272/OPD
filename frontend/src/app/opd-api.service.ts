import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Patient {
  id: number;
  name: string;
  gender: 'FEMALE' | 'MALE' | 'OTHER';
  age: number;
  phoneNumber: string;
}

export interface Visit {
  id: number;
  patient: Patient;
  doctorName: string;
  appointmentAt: string;
  status: 'SCHEDULED' | 'COMPLETED';
}

export interface Consultation {
  id: number;
  appointment: Visit;
  bloodPressure: string;
  temperature: number;
  notes: string;
  completedAt: string;
}

@Injectable({ providedIn: 'root' })
export class OpdApiService {
  private readonly baseUrl = '/api';

  constructor(private readonly http: HttpClient) {}

  getPatients(search = ''): Observable<Patient[]> {
    const params = search.trim() ? new HttpParams().set('search', search.trim()) : undefined;
    return this.http.get<Patient[]>(`${this.baseUrl}/patients`, { params });
  }

  createPatient(patient: Omit<Patient, 'id'>): Observable<Patient> {
    return this.http.post<Patient>(`${this.baseUrl}/patients`, patient);
  }

  getTodayAppointments(): Observable<Visit[]> {
    return this.http.get<Visit[]>(`${this.baseUrl}/appointments`);
  }

  createAppointment(appointment: {
    patientId: number | null;
    doctorName: string;
    appointmentAt: string;
  }): Observable<Visit> {
    return this.http.post<Visit>(`${this.baseUrl}/appointments`, appointment);
  }

  completeConsultation(
    appointmentId: number,
    consultation: { bloodPressure: string; temperature: number | null; notes: string },
  ): Observable<Consultation> {
    return this.http.post<Consultation>(
      `${this.baseUrl}/appointments/${appointmentId}/consultation`,
      consultation,
    );
  }

  getConsultations(patientId: number): Observable<Consultation[]> {
    return this.http.get<Consultation[]>(`${this.baseUrl}/patients/${patientId}/consultations`);
  }
}
