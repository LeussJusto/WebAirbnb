import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Star, MapPin, Bed, Bath, Wifi, Car, Coffee, Flame,
  Waves, Eye, ArrowLeft, Heart, Share
} from 'lucide-react';
import { useAccommodations } from '../hooks/useAccommodations';
import { useAuth } from '../hooks/useAuth';
import axios from 'axios';

const amenityIcons = {
  'WiFi': <Wifi className="h-5 w-5" />,
  'Cocina': <Coffee className="h-5 w-5" />,
  'Chimenea': <Flame className="h-5 w-5" />,
  'Tina caliente': <Waves className="h-5 w-5" />,
  'Piscina': <Waves className="h-5 w-5" />,
  'Estacionamiento': <Car className="h-5 w-5" />,
  'Vista al mar': <Eye className="h-5 w-5" />,
  'Vista a la montaña': <Eye className="h-5 w-5" />,
  'Vista a la ciudad': <Eye className="h-5 w-5" />,
  'Vista al bosque': <Eye className="h-5 w-5" />,
};

export default function AccommodationDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getAccommodationById } = useAccommodations();
  const { user } = useAuth();

  const [selectedImage, setSelectedImage] = useState(0);
  const [checkIn, setCheckIn] = useState('');
  const [checkOut, setCheckOut] = useState('');
  const [guests, setGuests] = useState(1);
  const [isBooking, setIsBooking] = useState(false);

  const accommodation = getAccommodationById(id!);

  if (!accommodation) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h2 className="text-2xl font-bold mb-4">Alojamiento no encontrado</h2>
          <button
            onClick={() => navigate('/accommodations')}
            className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
          >
            Volver
          </button>
        </div>
      </div>
    );
  }

  const nights = checkIn && checkOut
    ? Math.ceil((new Date(checkOut).getTime() - new Date(checkIn).getTime()) / (1000 * 60 * 60 * 24))
    : 0;

  const totalPrice = nights * accommodation.price;

  const handleBooking = async () => {
    if (!user) {
      navigate('/login');
      return;
    }

    if (!checkIn || !checkOut) {
      alert('Por favor selecciona fechas válidas');
      return;
    }

    try {
      setIsBooking(true);

      const token = localStorage.getItem('token'); // asegúrate de guardar tu JWT en el login

      const bookingData = {
        accommodationId: accommodation.id,
        checkIn,
        checkOut,
      };

      await axios.post('http://localhost:8080/api/bookings', bookingData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      navigate('/bookings');
    } catch (error) {
      console.error('Error al guardar la reserva:', error);
      alert('Ocurrió un error al guardar la reserva');
    } finally {
      setIsBooking(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Título */}
        <div className="flex justify-between items-center mb-6">
          <button onClick={() => navigate(-1)} className="flex items-center space-x-2 text-gray-600 hover:text-gray-900">
            <ArrowLeft className="h-5 w-5" />
            <span>Volver</span>
          </button>
          <div className="flex items-center space-x-4">
            <button className="border px-4 py-2 rounded-lg text-gray-700 hover:bg-gray-100"><Share className="h-5 w-5" /></button>
            <button className="border px-4 py-2 rounded-lg text-gray-700 hover:bg-gray-100"><Heart className="h-5 w-5" /></button>
          </div>
        </div>

        <h1 className="text-3xl font-bold mb-2">{accommodation.title}</h1>
        <div className="flex space-x-4 text-gray-500 mb-6">
          <div className="flex items-center space-x-1">
            <Star className="h-5 w-5 text-yellow-400 fill-current" />
            <span>{accommodation.rating}</span>
            <span>({accommodation.reviews} reseñas)</span>
          </div>
          <div className="flex items-center space-x-1">
            <MapPin className="h-5 w-5" />
            <span>{accommodation.location}</span>
          </div>
        </div>

        {/* Imágenes */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-8">
          <img src={accommodation.images[selectedImage]} alt="Main" className="w-full h-96 object-cover rounded-xl" />
          <div className="grid grid-cols-2 gap-4">
            {accommodation.images.slice(0, 4).map((img, idx) => (
              <img
                key={idx}
                src={img}
                alt={`img-${idx}`}
                className={`w-full h-40 object-cover rounded-lg cursor-pointer ${selectedImage === idx ? 'ring-2 ring-blue-500' : ''}`}
                onClick={() => setSelectedImage(idx)}
              />
            ))}
          </div>
        </div>

        {/* Detalles + Reserva */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white p-6 rounded-xl shadow-sm">
              <h3 className="text-xl font-semibold mb-4">Descripción</h3>
              <p className="text-gray-700">{accommodation.description}</p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm">
              <h3 className="text-xl font-semibold mb-4">Qué incluye</h3>
              <div className="grid grid-cols-2 gap-4">
                {accommodation.amenities.map((amenity, idx) => (
                  <div key={idx} className="flex items-center space-x-3">
                    {amenityIcons[amenity as keyof typeof amenityIcons] || <Wifi className="h-5 w-5" />}
                    <span>{amenity}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className="lg:col-span-1 sticky top-8">
            <div className="bg-white p-6 rounded-xl shadow-lg space-y-4">
              <div className="text-2xl font-bold text-gray-900">${accommodation.price} <span className="text-base text-gray-500">/ noche</span></div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-sm font-medium">Entrada</label>
                  <input type="date" value={checkIn} onChange={(e) => setCheckIn(e.target.value)} className="w-full border p-2 rounded-lg" />
                </div>
                <div>
                  <label className="text-sm font-medium">Salida</label>
                  <input type="date" value={checkOut} onChange={(e) => setCheckOut(e.target.value)} className="w-full border p-2 rounded-lg" />
                </div>
              </div>

              <div>
                <label className="text-sm font-medium">Personas</label>
                <select value={guests} onChange={(e) => setGuests(Number(e.target.value))} className="w-full border p-2 rounded-lg">
                  {Array.from({ length: accommodation.maxGuests }, (_, i) => i + 1).map(num => (
                    <option key={num} value={num}>{num}</option>
                  ))}
                </select>
              </div>

              {nights > 0 && (
                <div className="border-t pt-4 text-gray-700">
                  <div className="flex justify-between mb-2">
                    <span>${accommodation.price} × {nights} noches</span>
                    <span>${totalPrice}</span>
                  </div>
                  <div className="font-semibold text-lg flex justify-between">
                    <span>Total</span>
                    <span>${totalPrice}</span>
                  </div>
                </div>
              )}

              <button
                onClick={handleBooking}
                disabled={isBooking}
                className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                {isBooking ? 'Reservando...' : 'Reservar ahora'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
